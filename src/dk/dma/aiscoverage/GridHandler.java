package dk.dma.aiscoverage;

import java.util.HashMap;

public class GridHandler {

	public HashMap<Long, Grid> grids = new HashMap<Long, Grid>();
	
	private double latSize;
	private double lonSize;
	
	public GridHandler(double latSize, double lonSize){
		this.latSize = latSize;
		this.lonSize = lonSize;
	}
	
	/*
	 * Create grid associated to a specific transponder
	 */
	public void createGrid(Long bsMmsi){
		Grid grid = new Grid(bsMmsi, latSize, lonSize);
		grids.put(bsMmsi, grid);
	}
	
	public Grid getGrid(Long bsMmsi){
		return grids.get(bsMmsi);
	}
}
