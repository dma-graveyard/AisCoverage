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

import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.geotools.GeoConverter;
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
	private AbstractCoverageCalculator calculator = null;
	
	/*
	 * Timeout is in seconds. 
	 * If timeout is -1 reader will not stop until everything is read
	 * AisReader is only used to stop processing messages
	 * 
	 */
	public MessageHandler(int timeout, AisReader aisReader, AbstractCoverageCalculator calculator){
		this.timeout = timeout;
		this.aisReader = aisReader;
		this.calculator = calculator;
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
		
		if(GlobalSettings.getInstance().getLatSize() == -1){
			double cellInMeters= GlobalSettings.getInstance().getCellInMeters();
			GlobalSettings.getInstance().setLatSize(GeoConverter.metersToLatDegree(cellInMeters));
			GlobalSettings.getInstance().setLonSize(GeoConverter.metersToLonDegree(pos.getLatitude(), cellInMeters));
		}
		
		if(pos.getLatitude() < 37){
			System.out.println("bsmsi: " + bsMmsi);
			System.out.println("mmsi: " + posMessage.getUserId());
			System.out.println("lat: "+ pos.getLatitude());
			System.out.println("lon: " + pos.getLongitude());
			System.out.println();
		}

		// Check if grid exists (If a message with that bsmmsi has been received before)
		// Otherwise create a grid for corresponding base station
		Grid grid = gridHandler.getGrid(bsMmsi);
		if(grid == null){
			gridHandler.createGrid(bsMmsi);
			grid = gridHandler.getGrid(bsMmsi);
		}
		
		// Check which ship sent the message.
		// If it's the first message from that ship, create ship and put it in grid belonging to bsmmsi
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
		
		if(newMessage.ship.getMessages().peekLast() != null)
			newMessage.timeSinceLastMsg = (newMessage.timestamp.getTime() - newMessage.ship.getLastMessage().timestamp.getTime())/1000;
		
		//Filter messages, based on rules of thumb
		boolean filterMessage = calculator.filterMessage(newMessage);
		
		//calculate stuff
		if(filterMessage == false){
			calculator.calculateCoverage(newMessage);
		}
		
		//add ship to cell
		newMessage.cell.ships.put(newMessage.ship.mmsi, newMessage.ship);
				
		//Store received message as lastMessage in ship
		ship.setLastMessage(newMessage);

	}
	
	public long getCount() {
		return count;
	}

}
