package dk.dma.aiscoverage;

import java.util.HashMap;

import dk.frv.ais.geo.GeoLocation;

public class Grid {
	
	public HashMap<String, Cell> grid = new HashMap<String, Cell>();
	
	public HashMap<Long, Ship> ships = new HashMap<Long, Ship>();
	
	
	private Long bsMmsi;
	
	private double 	startLatitude,
					startLongitudeM;
	private double latSize;
	private double lonSize;
	public double	transponderLat,
					transponderLon; 
	private int 	gridWidth,
					gridHeight,
					NOofColumns,
					NOofRows;
	
	public Grid(Long bsMmsi, double latSize, double lonSize) {
		this.bsMmsi = bsMmsi;
		this.latSize = latSize;
		this.lonSize = lonSize;
	}
	
//	public Cell getCell(long cellId){
//		return grid.get(cellId);
//	}
	
	/*
	 * This is a suspect way of calculating global cellIDs
	 */
	public Cell getCell(double latitude, double longitude){
		return grid.get(getCellId(latitude, longitude));
	}
	
	/*
	 * latitude is rounded down
	 * longitude is rounded up.
	 * The id is lat-lon-coords representing top-left point in cell
	 */
	public String getCellId(double latitude, double longitude){
		double lat;
		double lon;
		if(latitude < 0){
			latitude +=latSize;
			lat = (double)((int)(10000*((latitude)- (latitude % latSize))))/10000;
			
		}else{
			lat = (double)((int)(10000*((latitude)- (latitude % latSize))))/10000;
		}
		
		if(longitude < 0){
			lon = (double)((int)(10000*(longitude - (longitude % lonSize))))/10000;
			
		}else{
			longitude -=lonSize;
			lon = (double)((int)(10000*(longitude - (longitude % lonSize))))/10000;
		}
		
		String cellId =  lat+"_"+lon;	
		return cellId;
	}
	
	public void createCell(double latitude, double longitude){
		Cell cell = new Cell();
		cell.id=getCellId(latitude, longitude);
		cell.latitude = (double)((int)(10000*(latitude - (latitude % latSize))))/10000;
		cell.longitude = (double)((int)(10000*(longitude - (longitude % lonSize))))/10000;
		grid.put(cell.id, cell);
	}
	
	/*
	 * Create ship
	 */
	public void createShip(Long mmsi){
		Ship ship = new Ship(mmsi);
		ships.put(mmsi, ship);
	}
	
	public Ship getShip(Long mmsi){
		return ships.get(mmsi);
	}
}
