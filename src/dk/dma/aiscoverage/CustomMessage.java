package dk.dma.aiscoverage;

import java.util.Date;
import java.util.HashMap;

import dk.frv.ais.message.AisPositionMessage;

public class CustomMessage {
	public AisPositionMessage message = null;
	Date timestamp = null;
	public Grid grid;
	public Ship ship;
	public Cell cell;
	public long timeSinceLastMsg;
}
