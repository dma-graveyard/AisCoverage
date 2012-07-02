/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.aiscoverage;

import java.util.Date;

import org.apache.log4j.Logger;

import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.proprietary.IProprietarySourceTag;

/**
 * Class for handling incoming AIS messages
 */
public class MessageHandler implements IAisHandler {
	
	private static Logger LOG = Logger.getLogger(MessageHandler.class);
	
	private long count = 0;

	/**
	 * Message for receiving AIS messages
	 */
	@Override
	public void receive(AisMessage aisMessage) {
		AisPositionMessage posMessage = null;
		GeoLocation pos = null;
		Long bsMmsi = null;
		Date timestamp = null;
		Country srcCountry = null;

		// Get source tag properties
		IProprietarySourceTag sourceTag = aisMessage.getSourceTag();
		if (sourceTag != null) {
			bsMmsi = sourceTag.getBaseMmsi();
			timestamp = sourceTag.getTimestamp();
			srcCountry = sourceTag.getCountry();
		}
		
		// What to do if no bsMmsi or timestamp?
		if (bsMmsi == null || timestamp == null) {
			return;
		}
				
		// Handle position messages
		if (aisMessage instanceof AisPositionMessage) {
			posMessage = (AisPositionMessage)aisMessage;
		} else {
			return;
		}
		
		// Increment count
		count++;
		
		// Validate postion
		if (!posMessage.isPositionValid()) {
			return;
		}
		
		// Get location
		pos = posMessage.getPos().getGeoLocation();

		// Examples
		LOG.debug("----");
		LOG.debug("BS      : " + bsMmsi);
		LOG.debug("Country : " + ((srcCountry != null) ? srcCountry.getTwoLetter() : "null"));
		LOG.debug("mmsi    : " + posMessage.getUserId());
		LOG.debug("position: " + pos);
		LOG.debug("sog     : " + posMessage.getSog());

		// Do dataprocessing here

	}
	
	public long getCount() {
		return count;
	}

}
