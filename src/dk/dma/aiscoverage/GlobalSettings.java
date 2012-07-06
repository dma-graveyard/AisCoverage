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

public class GlobalSettings {
	
	//global settings
	private double lonSize = -1;
	private double latSize = -1;
	private double cellInMeters = 5000;
	
	public double getCellInMeters() {
		return cellInMeters;
	}

	public void setCellInMeters(double cellInMeters) {
		this.cellInMeters = cellInMeters;
	}

	//Getters and setters
	public int getMessagesPerShipThreshold() {
		return messagesPerShipThreshold;
	}

	public void setMessagesPerShipThreshold(int messagesPerShipThreshold) {
		this.messagesPerShipThreshold = messagesPerShipThreshold;
	}

	private int messagesPerShipThreshold;

	
	public double getLonSize() {
		return lonSize;
	}

	public void setLonSize(double lonSize) {
		this.lonSize = lonSize;
	}

	public double getLatSize() {
		return latSize;
	}

	public void setLatSize(double latSize) {
		this.latSize = latSize;
	}
	
	//Singleton stuff
	private static GlobalSettings singletonObject;

	private GlobalSettings() {

	}
	
	public static synchronized GlobalSettings getInstance() {
		if (singletonObject == null) {
			singletonObject = new GlobalSettings();
		}
		return singletonObject;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
