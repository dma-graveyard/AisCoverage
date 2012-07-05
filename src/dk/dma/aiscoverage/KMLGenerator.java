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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class KMLGenerator {

	public static void generateKML(Collection<Grid> grids, String path) {
		FileWriter fstream = null;
		BufferedWriter out = null;

		try {
			fstream = new FileWriter(path);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
		}

			writeLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", out);
			writeLine("<kml>", out);
			writeLine("<Document>", out);
			writeLine("<name>AIS Coverage</name>", out);
			writeLine("<open>1</open>", out);
			writeLine("<Style id=\"redStyle\">", out);
			writeLine("	<IconStyle>", out);
			writeLine("		<scale>1.3</scale>", out);
			writeLine("		<Icon>", out);
			writeLine("			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>", out);
			writeLine("		</Icon>", out);
			writeLine("		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>", out);
			writeLine("	</IconStyle>", out);
			writeLine("	<LineStyle>", out);
			writeLine("		<color>ff0000ff</color>", out);
			writeLine("	</LineStyle>", out);
			writeLine("	<PolyStyle>", out);
			writeLine("		<color>ff0000ff</color>", out);
			writeLine("	</PolyStyle>", out);
			writeLine("</Style>", out);
			writeLine("<Style id=\"orangeStyle\">", out);
			writeLine("	<IconStyle>", out);
			writeLine("		<scale>1.3</scale>", out);
			writeLine("		<Icon>", out);
			writeLine("			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>", out);
			writeLine("		</Icon>", out);
			writeLine("		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>", out);
			writeLine("	</IconStyle>", out);
			writeLine("	<LineStyle>", out);
			writeLine("		<color>ff00aaff</color>", out);
			writeLine("	</LineStyle>", out);
			writeLine("	<PolyStyle>", out);
			writeLine("		<color>ff00aaff</color>", out);
			writeLine("	</PolyStyle>", out);
			writeLine("</Style>", out);
			writeLine("<Style id=\"greenStyle\">", out);
			writeLine("	<IconStyle>", out);
			writeLine("		<scale>1.3</scale>", out);
			writeLine("		<Icon>", out);
			writeLine("			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>", out);
			writeLine("		</Icon>", out);
			writeLine("		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>", out);
			writeLine("	</IconStyle>", out);
			writeLine("	<LineStyle>", out);
			writeLine("		<color>ff00ff00</color>", out);
			writeLine("	</LineStyle>", out);
			writeLine("	<PolyStyle>", out);
			writeLine("	<color>ff00ff55</color>", out);
			writeLine("</PolyStyle>", out);
			writeLine("</Style>", out);

			for (Grid grid : grids) {
				generateGrid(grid.bsMmsi, grid.grid.values(), out);
			}

			writeLine("</Document>", out);
			writeLine("</kml>", out);

		
	}

	private static void writeLine(String line, BufferedWriter out){
		try{
			out.write(line + "\n");
			out.flush();
		}catch(Exception e){
			System.out.println("baaah");
		}
	}
	private static void generateGrid(Long bsMmsi, Collection<Cell> cells,
			BufferedWriter out) {

			writeLine("<Folder>", out);
			writeLine("<name>" + bsMmsi + "</name>", out);
			writeLine("<open>1</open>", out);
			for (Cell cell : cells) {

				//We ignore cells, where average number of messages, is below 10 per ship
				//Maybe there is a bug in AISMessage system, that assign some messages to wrong Base Stations
				//Bug found and fixed
//				if (cell.NOofReceivedSignals / cell.ships.size() > 10) {
					
					if (cell.getCoverage() > 0.8) { // green
						generatePlacemark("#greenStyle", cell, 300, out);
					} else if (cell.getCoverage() > 0.5) { // orange
						generatePlacemark("#orangeStyle", cell, 200, out);
					} else { // red
						generatePlacemark("#redStyle", cell, 100, out);
					}
					
//				}

			}

			writeLine("</Folder>", out);

	}

	private static void generatePlacemark(String style, Cell cell, int z,
			BufferedWriter out) {
			GlobalSettings settings = GlobalSettings.getInstance();
			
			writeLine("<Placemark>", out);
			writeLine("<name>" + cell.id + "</name>", out);
			writeLine("<styleUrl>" + style + "</styleUrl>", out);
			writeLine("<Polygon>", out);
			writeLine("<altitudeMode>relativeToGround</altitudeMode>", out);
			writeLine("<tessellate>1</tessellate>", out);
			writeLine("<outerBoundaryIs>", out);
			writeLine("<LinearRing>", out);
			writeLine("<coordinates>", out);

			writeLine(		cell.longitude + "," + cell.latitude + "," + z+ " " + 
							(cell.longitude + settings.getLonSize()) + "," + cell.latitude + ","  + z + " " + 
							(cell.longitude + settings.getLonSize()) + "," + (cell.latitude + settings.getLatSize()) + "," + z + " " + 
							cell.longitude + "," + (cell.latitude + settings.getLatSize()) + "," + z, out);


			writeLine("</coordinates>", out);
			writeLine("</LinearRing>", out);
			writeLine("</outerBoundaryIs>", out);
			writeLine("</Polygon>", out);
			writeLine("</Placemark>", out);

	}

}
