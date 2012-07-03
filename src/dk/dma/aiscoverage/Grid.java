package dk.dma.aiscoverage;

import java.util.HashMap;

import dk.frv.ais.geo.GeoLocation;

public class Grid {
	
	public HashMap<Long, Cell> grid = new HashMap<Long, Cell>();
	
	private Long bsMmsi;
	
	private double 	startLatitude,
					cellSize,
					startLongitude; 
	private int 	gridWidth,
					gridHeight,
					NOofColumns,
					NOofRows;
	
	public Grid(Long bsMmsi, int cellSize) {
		this.bsMmsi = bsMmsi;
		this.cellSize = cellSize;
		this.NOofColumns = 127563200/cellSize;
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
	
	public Long getCellId(double latitude, double longitude){
		GeoLocation target = new GeoLocation();
		target.setLatitude(latitude);
		target.setLongitude(longitude);
		
		GeoLocation basePoint1 = new GeoLocation();
		basePoint1.setLatitude(0);
		basePoint1.setLongitude(longitude);
		
		GeoLocation basePoint2 = new GeoLocation();
		basePoint2.setLatitude(latitude);
		basePoint2.setLongitude(0);
		
		double x = basePoint1.getRhumbLineDistance(target);
		System.out.println("width:" + x);
		
		double y = basePoint2.getRhumbLineDistance(target);
		System.out.println("height:" + y);

		Long cellId =  ((long)(y/cellSize) * NOofColumns + ((long)(x/cellSize)+1));
		
		return cellId;
	}
	
	public void createCell(double latitude, double longitude){
		Cell cell = new Cell();
		cell.id=getCellId(latitude, longitude);
		grid.put(cell.id, cell);
	}
}
