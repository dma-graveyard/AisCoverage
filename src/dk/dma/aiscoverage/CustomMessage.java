package dk.dma.aiscoverage;

import java.util.Date;

import dk.frv.ais.message.AisPositionMessage;

public class CustomMessage {
	AisPositionMessage message = null;
	Date timestamp = null;
	public Grid grid;
	public Ship ship;
	public Cell cell;
	public long timeSinceLastMsg;
}