package dk.dma.aiscoverage.calculator;

import dk.dma.aiscoverage.Cell;
import dk.dma.aiscoverage.CustomMessage;

public class CoverageCalculatorAdvanced extends AbstractCoverageCalculator {

	
	/*
	 * A Parametric equation is used to find missing points' lat-lon coordinates between point1 and point2.
	 */
	@Override
	public void calculateCoverage(CustomMessage m2) {
		
		CustomMessage m1 = m2.ship.getLastMessage();
		Long p1Time = m1.timestamp.getTime();
		Long p2Time = m2.timestamp.getTime();
		double p1Lat = m1.message.getPos().getGeoLocation().getLatitude();
		double p1Lon = m1.message.getPos().getGeoLocation().getLongitude();
		double p2Lat = m2.message.getPos().getGeoLocation().getLatitude();
		double p2Lon = m2.message.getPos().getGeoLocation().getLongitude();
		
		double timeSinceLastMessage = getTimeDifference(p1Time, p2Time);
		int sog = m2.message.getSog()/10;
		int expectedTransmittingFrequency = getExpectedTransmittingFrequency(sog);
		
		// Calculate missing messages
		// A Parametric equation is used to find missing points' lat-lon coordinates between point1 and point2.
		// These points are not converted to utm-x-y coordinates before calculating missing points.
		// This would be a more accurate method, but it probably doesn't matter when distances are 
		// relatively small like in this case.
		int missingMessages; 
		if(timeSinceLastMessage > expectedTransmittingFrequency) {
			
			// Number of missing points between the two points
			missingMessages = (int) (Math.round((double)timeSinceLastMessage/(double)expectedTransmittingFrequency)-1);
			
			// Finds lat/lon of each missing point and adds "missing signal" to corresponding cell
			for (int i = 1; i <= missingMessages; i++) {

				double latMissing = getLat(i*expectedTransmittingFrequency,p1Time, p2Time, p1Lat, p2Lat);
				double lonMissing = getLon(i*expectedTransmittingFrequency,p1Time, p2Time, p1Lon, p2Lon);

				//Add number of missing messages to cell
				Cell c = m2.grid.getCell(latMissing, lonMissing);
				if(c == null){
					c = m2.grid.createCell(latMissing, lonMissing);
				}
					
				c.NOofMissingSignals++;
			}
			
		}
		
	}
	
	
	public double getLat(int seconds, Long p1Time, Long p2Time, double p1Lat, double p2Lat){
		double timeDiff = getTimeDifference(p1Time, p2Time);
		double latDifference = p2Lat - p1Lat;
		double latPerSec = latDifference/timeDiff;
		
		return p1Lat + (latPerSec * seconds);
	}
	public double getLon(int seconds, Long p1Time, Long p2Time, double p1Lon, double p2Lon){
		double timeDiff = getTimeDifference(p1Time, p2Time);
		double lonDifference = p2Lon - p1Lon;
		double lonPerSec = lonDifference/timeDiff;
		
		return p1Lon + (lonPerSec * seconds);
	}

}
