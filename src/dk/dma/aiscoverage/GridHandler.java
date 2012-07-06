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

public class GridHandler {

	public HashMap<Long, Grid> grids = new HashMap<Long, Grid>();
	
	public GridHandler(){
	}
	
	/*
	 * Create grid associated to a specific transponder
	 */
	public void createGrid(Long bsMmsi){
		Grid grid = new Grid(bsMmsi);
		grids.put(bsMmsi, grid);
	}
	
	public Grid getGrid(Long bsMmsi){
		return grids.get(bsMmsi);
	}
	
	public double metersToLonDegree(double latitude, double meters){	
		
		//calculate length of 1 degree lon
		double latRad = Math.toRadians(latitude);
		double a = 6378137;
		double b = 6356752.3142;
		double ee = ( (a*a)-(b*b) )/(a*a);
		double oneDegreeLength = ( Math.PI * a * Math.cos(latRad) )/ ( 180 * Math.pow(1 - ee*( (Math.sin(latRad) * Math.sin(latRad) )), 0.5) );
		double lonDegree = (1/oneDegreeLength)*meters;
		
		return lonDegree;
	}
	public double metersToLatDegree(double meters){
		return ((double)1/111000)*meters;
	}
}
