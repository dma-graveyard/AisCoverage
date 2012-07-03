package dk.dma.aiscoverage;

public class Cell {

	Long	
			NOofReceivedSignals=0L, 
			NOofMissingSignals=0L;
	
	double 	distanceToNearestBasestation, 
			latitude,
			longitude;
	
	String id;
	
	public double getCoverage(){
		return NOofReceivedSignals/(NOofReceivedSignals+NOofMissingSignals);
	}
}
