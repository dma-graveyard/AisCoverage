package dk.dma.aiscoverage;

public class Cell {

	Long	
			NOofReceivedSignals=0L, 
			NOofMissingSignals=0L;
	
	double 	distanceToNearestBasestation, 
			latitude,
			longitude;
	
	String id;
	
	public long getTotalNumberOfMessages(){
		return NOofReceivedSignals+NOofMissingSignals;
	}
	public double getCoverage(){
		return (double)NOofReceivedSignals/ (double)getTotalNumberOfMessages();
	}
}
