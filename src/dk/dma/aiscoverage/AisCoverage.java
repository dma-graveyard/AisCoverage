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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import dk.frv.ais.proprietary.DmaFactory;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.AisReader;
import dk.frv.ais.reader.AisStreamReader;
import dk.frv.ais.reader.RoundRobinAisTcpReader;

public class AisCoverage {
	
	private static Logger LOG;

	/**
	 * Application to make AIS coverage analysis
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		DOMConfigurator.configure("log4j.xml");
		LOG = Logger.getLogger(AisCoverage.class);
		LOG.info("Starting AisCoverage");
		
		// Read command line arguments
		String filename = null;
		String hostPort = null;

		if (args.length < 2) {
			usage();
			System.exit(1);
		}

		
		
		System.out.println("Kasper branch!");
		
		int i = 0;
		while (i < args.length) {
			if (args[i].indexOf("-t") >= 0) {
				hostPort = args[++i];
			} else if (args[i].indexOf("-f") >= 0) {
				filename = args[++i];
			}
			i++;
		}

		if (filename == null && hostPort == null) {
			usage();
			System.exit(1);
		}

		// Use TCP or file as source
		AisReader aisReader;
		if (filename != null) {
			LOG.debug("Using file source: " + filename);
			aisReader = new AisStreamReader(new FileInputStream(filename));
		} else {
			LOG.debug("Using TCP source: " + hostPort);
			RoundRobinAisTcpReader rrAisReader = new RoundRobinAisTcpReader();
			rrAisReader.setCommaseparatedHostPort(hostPort);
			aisReader = rrAisReader;
		}

		// Register proprietary handlers (optional)
		aisReader.addProprietaryFactory(new DmaFactory());
		aisReader.addProprietaryFactory(new GatehouseFactory());

		// Make handler instance
		MessageHandler messageHandler = new MessageHandler();
		
		// Register handler and start reader
		aisReader.registerHandler(messageHandler);
		aisReader.start();

		aisReader.join();
		
		Grid grid = messageHandler.gridHandler.getGrid((long) 2190049);
		System.out.println("number of cells: " + grid.grid.size());
//		System.out.println("transponder lat: " + grid.);
		
		Collection<Cell> cells = grid.grid.values();
		for (Cell cell : cells) {
			if(cell.getTotalNumberOfMessages() < 10)
				System.out.println("less than 10 registrants");
			else
				System.out.println("lat: "+ cell.latitude + " lon: "+cell.longitude +" Coverage: " + cell.getCoverage());
		}
		
//		double[] coords = MercatorConverter.merc(10, 85);
//		System.out.println(coords[0]);
//		System.out.println(coords[1]);
		
//		GridHandler handler = new GridHandler(0.05);
//		handler.createGrid(1L);
//		Grid g = handler.getGrid(1L);
//		System.out.println(g.getCellId(1.20003, 1.2));
//		double test = 1.175;
//		System.out.println( (double)((int)(10000*(test - (test % 0.05))))/10000 );
		
	
		

	}

	public static void usage() {
		System.out.println("Usage: AisCoverage <-t|-f> <filename/host1:port1,...,hostN,portN>");
		System.out.println("\t-t TCP round robin connection to host1:port1 ... hostN:portN");
		System.out.println("\t-f Read from file filename");
	}

}
