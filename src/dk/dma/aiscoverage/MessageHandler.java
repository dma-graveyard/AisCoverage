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

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.proprietary.IProprietarySourceTag;

/**
 * Class for handling incoming AIS messages
 */
public class MessageHandler implements IAisHandler {
	
	private static Logger LOG = Logger.getLogger(MessageHandler.class);
	
	
	private long count = 0;
	
	public GridHandler gridHandler = new GridHandler(0.1, 0.2);


	/**
	 * Message for receiving AIS messages
	 */
	@Override
	public void receive(AisMessage aisMessage) {
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

		// Examples
//		LOG.debug("----");
//		LOG.debug("BS      : " + bsMmsi);
//		LOG.debug("Country : " + ((srcCountry != null) ? srcCountry.getTwoLetter() : "null"));
//		LOG.debug("mmsi    : " + posMessage.getUserId());
//		LOG.debug("position: " + pos);
//		LOG.debug("sog     : " + posMessage.getSog());
//		LOG.debug("Timestamp   : " + timestamp);

//		System.out.println(bsMmsi);
		
		// Do dataprocessing here
		if(bsMmsi == 2190071){
			if(pos.getLongitude() > 12)
				System.out.println("D篤篤電電電電電電");
//			System.out.println("<Placemark>");
//			System.out.println("<name>Polygon_red</name>");
//			System.out.println("<styleUrl>#greenStyle</styleUrl>");
//			System.out.println("<Polygon>");
//			System.out.println("<tessellate>1</tessellate>");
//			System.out.println("<outerBoundaryIs>");
//			System.out.println("<LinearRing>");
//			System.out.println("<coordinates>");
//			
//			System.out.print(pos.getLongitude()+","+pos.getLatitude()+","+0+" ");
//			System.out.print((pos.getLongitude() + 0.05)+","+pos.getLatitude()+","+0+" ");
//			System.out.print((pos.getLongitude() + 0.05)+","+(pos.getLatitude() + 0.05)+","+0+" ");
//			System.out.println(pos.getLongitude()+","+(pos.getLatitude() + 0.05)+","+0);
//	
//			System.out.println("</coordinates>");
//			System.out.println("</LinearRing>");
//			System.out.println("</outerBoundaryIs>");
//			System.out.println("</Polygon>");
//			System.out.println("</Placemark>");
		}
		
		
		
		//Check if grid exist (If a message with that bsmmsi has been received before)
		//otherwise create a grid
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
		
		//Store received message as lastMessage in ship
		ship.setLastMessage(newMessage);

		
		
		
//		System.out.println("shiops: " + gridHandler.ships.size());
//		System.out.println("modtagere: " + gridHandler.grids.size());
//		System.out.println(posMessage.getPos().getLatitude());
//		System.out.println("NO of cells: " + grid.grid.size());
//		
//		cell.NOofReceivedSignals++;
//		System.out.println("Received in that cell: " + cell.NOofReceivedSignals);
//		grid.getCell(pos.getLatitude(), pos.getLongitude());

		

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
		
		//Test print outs
//		System.out.println("cell coverage: " + customMessage.cell.getCoverage());
//		System.out.println(customMessage.ship.mmsi +"\t missingMessages \t" + missingMessages);
//		System.out.println(customMessage.ship.mmsi +"\t expectedFreq \t" + expectedTransmittingFrequency);
//		System.out.println(customMessage.ship.mmsi +"\t distance \t" + distance);
//		System.out.println(customMessage.ship.mmsi +"\t SOG \t" + customMessage.message.getSog());
//		System.out.println(customMessage.ship.mmsi +"Seconds since last message \t" + customMessage.timeSinceLastMsg);
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
