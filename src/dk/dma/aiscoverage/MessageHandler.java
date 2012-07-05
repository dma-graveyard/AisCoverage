/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.aiscoverage;

import java.util.Date;
import org.apache.log4j.Logger;
import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.proprietary.IProprietarySourceTag;
import dk.frv.ais.reader.AisReader;

/**
 * Class for handling incoming AIS messages
 */
public class MessageHandler implements IAisHandler {
	
	private static Logger LOG = Logger.getLogger(MessageHandler.class);
	private long count = 0;
	private int timeout = -1;
	public GridHandler gridHandler = new GridHandler();
	private Date starttime = new Date();
	private AisReader aisReader = null;
	
	/*
	 * Timeout is in seconds. 
	 * If timeout is -1 reader will not stop until everything is read
	 */
	public MessageHandler(int timeout, AisReader aisReader){
		this.timeout = timeout;
		this.aisReader = aisReader;
	}


	/**
	 * Message for receiving AIS messages
	 */
	@Override
	public void receive(AisMessage aisMessage) {
		
		//Check timeout
		Date now = new Date();
		int timeSinceStart = (int) ((now.getTime() - starttime.getTime()) / 1000);
		if(timeout != -1 && timeSinceStart > timeout)
			aisReader.stop();
		
		AisPositionMessage posMessage = null;
		GeoLocation pos = null;
		Long bsMmsi = null;
		Date timestamp = null;
		Country srcCountry = null;

		// Get source tag properties
		IProprietarySourceTag sourceTag = aisMessage.getSourceTag();
		if (sourceTag != null) {
			bsMmsi = sourceTag.getBaseMmsi();
			timestamp = sourceTag.getTimestamp();
			srcCountry = sourceTag.getCountry();
		}
		
		// What to do if no bsMmsi or timestamp?
		if (bsMmsi == null || timestamp == null) {
			return;
		}
				
		// Handle position messages
		if (aisMessage instanceof AisPositionMessage) {
			posMessage = (AisPositionMessage)aisMessage;
		} else {
			return;
		}
		
		// Increment count
		count++;
		
		// Validate postion
		if (!posMessage.isPositionValid()) {
			return;
		}
		
		// Get location
		pos = posMessage.getPos().getGeoLocation();

		//Check if grid exists (If a message with that bsmmsi has been received before)
		//otherwise create a grid for corresponding base station
		Grid grid = gridHandler.getGrid(bsMmsi);
		if(grid == null){
			gridHandler.createGrid(bsMmsi);
			grid = gridHandler.getGrid(bsMmsi);
		}
		
		Ship ship = grid.getShip(posMessage.getUserId());
		if(ship == null){
			grid.createShip(posMessage.getUserId());
			ship = grid.getShip(posMessage.getUserId());
		}
		
		Cell cell = grid.getCell(pos.getLatitude(), pos.getLongitude());
		if(cell == null){
			grid.createCell(pos.getLatitude(), pos.getLongitude());
			cell = grid.getCell(pos.getLatitude(), pos.getLongitude());
		}
		
		CustomMessage newMessage = new CustomMessage();
		newMessage.message = posMessage;
		newMessage.timestamp = timestamp;
		newMessage.grid = grid;
		newMessage.ship = ship;
		newMessage.cell = cell;
		newMessage.cell.NOofReceivedSignals++;
		
		if(newMessage.ship.getLastMessage() != null)
			newMessage.timeSinceLastMsg = (newMessage.timestamp.getTime() - newMessage.ship.getLastMessage().timestamp.getTime())/1000;
		
		//Filter messages, based on rules of thumb
		boolean filterMessage = filterMessage(newMessage);
		
		//calculate stuff
		if(filterMessage == false){
			calculateCoverage(newMessage);
		}
		
		//add ship to cell
		newMessage.cell.ships.put(newMessage.ship.mmsi, newMessage.ship);
				
		//Store received message as lastMessage in ship
		ship.setLastMessage(newMessage);

	}
	
	private void calculateCoverage(CustomMessage customMessage){
		
		//Calculate distance since last message
		GeoLocation oldPos = customMessage.ship.getLastMessage().message.getPos().getGeoLocation();
		GeoLocation pos = customMessage.message.getPos().getGeoLocation();
		double distance = oldPos.getRhumbLineDistance(pos);
		
		//Determine expected transmitting frequency
		int expectedTransmittingFrequency;
		if(customMessage.message.getSog()/10 < 14)
			expectedTransmittingFrequency = 10;
		else if(customMessage.message.getSog()/10 < 23)
			expectedTransmittingFrequency = 6;
		else 
			expectedTransmittingFrequency = 2;
		
		//Calculate missing messages
		int missingMessages; 
		if(customMessage.timeSinceLastMsg <= expectedTransmittingFrequency) //We're good
			missingMessages = 0;
		else{
			missingMessages = (int) (Math.round((double)customMessage.timeSinceLastMsg/(double)expectedTransmittingFrequency)-1);
		}
		
		
		//Add number of missing and actual received messages to cell
		customMessage.cell.NOofMissingSignals += missingMessages;
		
	}

	
	private boolean filterMessage(CustomMessage customMessage){
		boolean filterMessage = false;
		if(customMessage.message.getSog()/10 < 3 || customMessage.message.getSog()/10 > 50)
			filterMessage = true;
		if(customMessage.message.getCog() == 360)
			filterMessage = true;
		
		//if this is the first message for a ship, we don't calculate coverage
		if(customMessage.ship.getLastMessage() == null) {
			filterMessage = true;
		}else{
			//If lastPoint and newPoint is not in same cell, we ignore the message for now
			GeoLocation pos = customMessage.message.getPos().getGeoLocation();
			Cell cell = customMessage.grid.getCell(pos.getLatitude(), pos.getLongitude());
			GeoLocation oldPos = customMessage.ship.getLastMessage().message.getPos().getGeoLocation();
			if(!customMessage.grid.getCellId(oldPos.getLatitude(), oldPos.getLongitude()).equals(cell.id)){
				filterMessage = true;
//				System.out.println("New cell, lets ignore");
			}else{
//				System.out.println(customMessage.ship.mmsi +"\t WEEEEEH, same cell, hurrayyy");
			}
			
			//If time since last message is > 30 minutes, we filter
			if(customMessage.timeSinceLastMsg > 1800)
				filterMessage = true;
			
			//Check if ship is turning (ignore for now)
//			if(customMessage.message.getCog())
//				System.out.println("COGnew: "+customMessage.message.getCog());
//				System.out.println("COGold: "+customMessage.ship.getLastMessage().message.getCog());		
			}
		
		return filterMessage;
	}
	
	public long getCount() {
		return count;
	}

}
