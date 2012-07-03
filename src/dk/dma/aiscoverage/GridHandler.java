package dk.dma.aiscoverage;

import java.util.HashMap;

public class GridHandler {

	public HashMap<Long, Grid> grids = new HashMap<Long, Grid>();
	
	private double cellSize;
	
	public GridHandler(double cellSize){
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
}
