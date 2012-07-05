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
	private double lonSize = 0.2;
	private double latSize = 0.1;

	
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
