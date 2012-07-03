package dk.dma.aiscoverage;

import java.util.HashMap;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisPositionMessage;

public class Ship {
	
	private Long mmsi;
	private AisPositionMessage lastMessage;

	public Ship(Long mmsi) {
		this.mmsi = mmsi;
	}
	
	public void setLastMessage(AisPositionMessage message){
		this.lastMessage = message;
	}
	


}
