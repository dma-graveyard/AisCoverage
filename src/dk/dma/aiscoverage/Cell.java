package dk.dma.aiscoverage;

public class Cell {

	Long	id,
			NOofReceivedSignals=0L, 
			NOofMissingSignals=0L;
	
	double 	distanceToNearestBasestation, 
			coverage,
			latitude,
			longitude;
}
