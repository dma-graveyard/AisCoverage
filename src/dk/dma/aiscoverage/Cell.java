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

import java.util.HashMap;

public class Cell {
	
	HashMap<Long, Ship> ships = new HashMap<Long, Ship>();
	public Long NOofReceivedSignals=0L; 
	public Long NOofMissingSignals=0L;
	double latitude;
	double longitude;
	public String id;
	
	public long getTotalNumberOfMessages(){
		return NOofReceivedSignals+NOofMissingSignals;
	}
	public double getCoverage(){
		return (double)NOofReceivedSignals/ (double)getTotalNumberOfMessages();
	}
}
