package dk.dma.aiscoverage;

import java.util.Collection;

public class KMLGenerator {
	
	public static void generateKML(Collection<Cell> cells){
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		System.out.println("<kml>");
		System.out.println("<Document>");
	System.out.println("<name>Tre polygoner.kml</name>");
	System.out.println("<open>1</open>");
	System.out.println("<Style id=\"redStyle\">");
	System.out.println("	<IconStyle>");
	System.out.println("		<scale>1.3</scale>");
	System.out.println("		<Icon>");
	System.out.println("			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>");
	System.out.println("		</Icon>");
	System.out.println("		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>");
	System.out.println("	</IconStyle>");
	System.out.println("	<LineStyle>");
	System.out.println("		<color>ff0000ff</color>");
	System.out.println("	</LineStyle>");
	System.out.println("	<PolyStyle>");
	System.out.println("		<color>ff0000ff</color>");
	System.out.println("	</PolyStyle>");
	System.out.println("</Style>");
	System.out.println("<Style id=\"orangeStyle\">");
	System.out.println("	<IconStyle>");
	System.out.println("		<scale>1.3</scale>");
	System.out.println("		<Icon>");
	System.out.println("			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>");
	System.out.println("		</Icon>");
	System.out.println("		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>");
	System.out.println("	</IconStyle>");
	System.out.println("	<LineStyle>");
	System.out.println("		<color>ff00aaff</color>");
	System.out.println("	</LineStyle>");
	System.out.println("	<PolyStyle>");
	System.out.println("		<color>ff00aaff</color>");
	System.out.println("	</PolyStyle>");
	System.out.println("</Style>");
	System.out.println("<Style id=\"greenStyle\">");
	System.out.println("	<IconStyle>");
	System.out.println("		<scale>1.3</scale>");
	System.out.println("		<Icon>");
	System.out.println("			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>");
	System.out.println("		</Icon>");
	System.out.println("		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>");
	System.out.println("	</IconStyle>");
	System.out.println("	<LineStyle>");
	System.out.println("		<color>ff00ff00</color>");
	System.out.println("	</LineStyle>");
	System.out.println("	<PolyStyle>");
	System.out.println("	<color>ff00ff55</color>");
	System.out.println("</PolyStyle>");
	System.out.println("</Style>");
	
	
	System.out.println("<Folder>");
	System.out.println("<name>Polygoner</name>");
	System.out.println("<open>1</open>");
	for (Cell cell : cells) {
		if(cell.getTotalNumberOfMessages() > 100){
			if(cell.getCoverage() > 0.8){ //green
				generatePlacemark("#greenStyle", cell);
			}else if(cell.getCoverage() > 0.5){ //orange
				generatePlacemark("#orangeStyle", cell);
			}else{ //red
				generatePlacemark("#redStyle", cell);
			}
		}
	}

	System.out.println("</Folder>");
	
	System.out.println("</Document>");
	System.out.println("</kml>");
	}
	
	private static void generatePlacemark(String style, Cell cell){
		System.out.println("<Placemark>");
		System.out.println("<name>Polygon_red</name>");
		System.out.println("<styleUrl>"+style+"</styleUrl>");
		System.out.println("<Polygon>");
		System.out.println("<tessellate>1</tessellate>");
		System.out.println("<outerBoundaryIs>");
		System.out.println("<LinearRing>");
		System.out.println("<coordinates>");
		
		System.out.print(cell.longitude+","+cell.latitude+","+0+" ");
		System.out.print((cell.longitude+0.2) + ","+cell.latitude+","+0+" ");
		System.out.print((cell.longitude + 0.2)+","+(cell.latitude + 0.1)+","+0+" ");
		System.out.println(cell.longitude+","+(cell.latitude + 0.1)+","+0);

		System.out.println("</coordinates>");
		System.out.println("</LinearRing>");
		System.out.println("</outerBoundaryIs>");
		System.out.println("</Polygon>");
		System.out.println("</Placemark>");
	}
	
}
