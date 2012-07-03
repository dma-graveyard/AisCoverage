package dk.dma.aiscoverage;

import java.util.HashMap;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisPositionMessage;

public class Ship {
	
	public Long mmsi;
	private CustomMessage lastMessage = null;

	public Ship(Long mmsi) {
		this.mmsi = mmsi;
	}
	
	public void setLastMessage(CustomMessage message){
		this.lastMessage = message;
	}
	public CustomMessage getLastMessage(){
		return lastMessage;
	}
	


}
