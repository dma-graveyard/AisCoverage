package dk.dma.aiscoverage.calculator;

import dk.dma.aiscoverage.Cell;
import dk.dma.aiscoverage.CustomMessage;
import dk.frv.ais.geo.GeoLocation;

public class CoverageCalculatorSimple extends AbstractCoverageCalculator{

	@Override
	public void calculateCoverage(CustomMessage customMessage) {
		customMessage.cell.NOofReceivedSignals++;
		//If lastPoint and newPoint is not in same cell, we ignore the message for now
				GeoLocation pos = customMessage.message.getPos().getGeoLocation();
				Cell cell = customMessage.grid.getCell(pos.getLatitude(), pos.getLongitude());
				GeoLocation oldPos = customMessage.ship.getLastMessage().message.getPos().getGeoLocation();
				if(!customMessage.grid.getCellId(oldPos.getLatitude(), oldPos.getLongitude()).equals(cell.id)){
					return;
				}
				
				//Calculate distance since last message
				double distance = oldPos.getRhumbLineDistance(pos);
				
				//Determine expected transmitting frequency
				int expectedTransmittingFrequency;
				if(customMessage.message.getSog()/10 < 14)
					expectedTransmittingFrequency = 10;
				else if(customMessage.message.getSog()/10 < 23)
					expectedTransmittingFrequency = 6;
				else 
					expectedTransmittingFrequency = 2;
				
				//Calculate missing messages
				int missingMessages; 
				if(customMessage.timeSinceLastMsg <= expectedTransmittingFrequency) //We're good
					missingMessages = 0;
				else{
					missingMessages = (int) (Math.round((double)customMessage.timeSinceLastMsg/(double)expectedTransmittingFrequency)-1);
				}
				
				
				//Add number of missing messages to cell
				customMessage.cell.NOofMissingSignals += missingMessages;
		
	}

}
