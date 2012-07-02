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
	
	private ArrayList<Cell> grid = new ArrayList<Cell>();
	private double 	startLatitude,
					startLongitude; 
	private int 	gridWidth, 
					gridHeight,
					cellSize,
					NOofColumns,
					NOofRows;
	
	
	

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
		LOG.debug("----");
		LOG.debug("BS      : " + bsMmsi);
		LOG.debug("Country : " + ((srcCountry != null) ? srcCountry.getTwoLetter() : "null"));
		LOG.debug("mmsi    : " + posMessage.getUserId());
		LOG.debug("position: " + pos);
		LOG.debug("sog     : " + posMessage.getSog());

		// Do dataprocessing here
		
		//convert to Lat/long to metric

	}
	
	public long getCount() {
		return count;
	}
	
	

	/*
	 * 
	 */
	public void initGrid(double latitude, double longitude, int width, int height, int cellSize) {
		this.startLatitude = latitude;
		this.startLongitude = longitude;
		this.gridWidth = width;
		this.gridHeight = height;
		this.cellSize = cellSize;
		
		this.NOofColumns =  width/cellSize;
		this.NOofRows = height/cellSize;
		
		int cellId = 1;
		for (int i = 0; i < NOofColumns; i++) {
			for (int j = 0; j < NOofRows; j++) {
				System.out.print(cellId+"\t");
				Cell c = new Cell();
				c.id = cellId;
				grid.add(c);
				cellId++;
			}	
			System.out.println();
		}
		
		
	}
	
	/*
	 * 
	 */
	public Cell getCell(double latitude, double longitude){
		GeoLocation lat = new GeoLocation();
		lat.setLatitude(latitude);
		
		GeoLocation lon = new GeoLocation();
		lon.setLatitude(longitude);
		
		GeoLocation startLat = new GeoLocation();
		startLat.setLatitude(startLatitude);
		
		GeoLocation startLon = new GeoLocation();
		startLon.setLatitude(startLongitude);
		
		
		
		double x = startLat.getGeodesicDistance(lat);
		System.out.println(x);
		double y = startLon.getGeodesicDistance(lon);
		System.out.println(y);
		
		//Checks if target is within grid
		if(x >= this.gridWidth || x < 0)
			return null;
		if(y >= this.gridHeight || y < 0 )
			return null;
		
		int cellId =  ((int)(y/cellSize) * NOofColumns + ((int)(x/cellSize)+1));
		return this.grid.get(cellId-1);
	}
	
	public class Cell{
		int		id,
				NOofReceivedSignals, 
				NOofMissingSignals;
		double 	distanceToNearestBasestation, 
				coverage;
	}

}
