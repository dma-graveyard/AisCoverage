package dk.dma.aiscoverage;

import java.util.HashMap;

public class GridHandler {

	public HashMap<Long, Grid> grids = new HashMap<Long, Grid>();
	public HashMap<Long, Ship> ships = new HashMap<Long, Ship>();
	
	private int cellSize;
	
	public GridHandler(int cellSize){
		this.cellSize = cellSize;
	}
	/*
	 * Create grid associated to a specific transponder
	 */
	public void createGrid(Long bsMmsi){
		Grid grid = new Grid(bsMmsi, cellSize);
		grids.put(bsMmsi, grid);
	}
	
	public Grid getGrid(Long bsMmsi){
		return grids.get(bsMmsi);
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
