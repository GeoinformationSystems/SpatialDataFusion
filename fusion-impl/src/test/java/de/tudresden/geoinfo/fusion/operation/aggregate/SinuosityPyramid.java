//package de.tudresden.gis.fusion.operation.aggregate;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Map;
//
//import org.junit.Test;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LineString;
//import com.vividsolutions.jts.geom.MultiLineString;
//
//import de.tudresden.gis.fusion.data.IData;
//import de.tudresden.gis.fusion.data.feature.geotools.GTVectorFeature;
//import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
//import de.tudresden.gis.fusion.data.literal.URILiteral;
//import de.tudresden.gis.fusion.operation.io.ShapefileParser;
//
//public class SinuosityPyramid {
//
//	@Test
//	public void test() throws FileNotFoundException{
//
//		double start = System.currentTimeMillis();
//
//		ShapefileParser parser = new ShapefileParser();
//		Map<String,IData> input = new HashMap<String,IData>();
//		input.put("IN_RESOURCE", new URILiteral(new File("D:/Geodaten/LfULG/Gew�sserstrukturkartierung", "Gewaessernetz.shp").toURI()));
//		Map<String,IData> output = parser.execute(input);
//		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
//		GTVectorFeature lockwitzbach = reference.elementById("Gewaessernetz.55");
//		LineString geometry = (LineString) ((MultiLineString) lockwitzbach.getDefaultGeometry()).getGeometryN(0);
//
//		int elementLength = 1;
//		LineString resampled = resample(geometry, elementLength);
//
//		System.out.println("Resampled Line into " + resampled.getNumPoints() + " segments; Runtime: " + ((System.currentTimeMillis() - start) / 1000) + "s");
//
//        PrintWriter pw = new PrintWriter(new File("C:/Users/Stefan/Desktop/pyramid_" + String.valueOf(elementLength) + "m.csv"));
//        DecimalFormat formatter = new DecimalFormat("#.####");
//
//        int length = resampled.getNumPoints() - 1;
//        int print = resampled.getNumPoints() / 10;
//        int progress = 10;
//
//        for(int i=2; i<length; i+=2){
//        	if(i >= print){
//        		System.out.print("..." + progress + " ");
//        		progress += 10;
//        		print += print;
//        	}
//        	StringBuilder sb = new StringBuilder();
//        	double[] sinuosities = calculateSinuosity(resampled, i, elementLength);
//    		for(double sinuosity : sinuosities){
//    			if(sinuosity != 0)
//    				sb.append(formatter.format(sinuosity));
//    	        sb.append(';');
//    		}
//    		sb.deleteCharAt(sb.length() - 1);
//    		sb.append('\n');
//    		pw.write(sb.toString());
//        }
//
//        pw.close();
//
////		double min = Double.MAX_VALUE;
////		double max = Double.MIN_VALUE;
////		Coordinate start = null, end = null;
////		for(Coordinate coord : resampled.getCoordinates()){
////			if(start == null)
////				start = coord;
////			else {
////				end = coord;
////				double dist = start.distance(end);
////				if(dist > max)
////					max = dist;
////				if(dist < min)
////					min = dist;
////				start = coord;
////			}
////		}
//
//		System.out.println("Finished! Runtime: " + ((System.currentTimeMillis() - start) / 1000) + "s");
//	}
//
//	private LineString resample(LineString line, double interval){
//		LinkedList<Coordinate> list = new LinkedList<Coordinate>();
//		double initialDistance = interval;
//		Coordinate start = null, end = null;
//		boolean first = true;
//		for(Coordinate coord : line.getCoordinates()){
//			if(first){
//				// add first coordinate
//				list.add(coord);
//				// set first coordinate as start
//				start = coord;
//				first = false;
//			}
//			else {
//				//set current coordinate as end
//				end = coord;
//				//get distance start - end
//				double distanceStartEnd = start.distance(end);
//				if(initialDistance < distanceStartEnd){
//					//first split at initialDistance
//					Coordinate split = splitLine(start, end, initialDistance);
//					list.add((Coordinate) split.clone());
//					//set last split to start
//					start = split;
//					//check rest
//					double rest = distanceStartEnd - initialDistance;
//					if(rest == interval){
//						//directly add coordinate
//						list.add(coord);
//						//set interval and rest distance to 0
//						initialDistance = 0;
//						rest = 0;
//					}
//					//further split until rest is lower than interval
//					while(rest > interval){
//						split = splitLine(start, end, interval);
//						list.add((Coordinate) split.clone());
//						//set last split to start
//						start = split;
//						//reduce rest
//						rest = rest - interval;
//					}
//					//set initial distance to interval - rest
//					initialDistance = interval - rest;
//				}
//				else if(initialDistance > distanceStartEnd){
//					//set intervalDistance to initialDistance - distanceStartEnd
//					initialDistance = initialDistance - distanceStartEnd;
//				}
//				else {
//					//directly add coordinate
//					list.add(coord);
//					//set interval distance to 0
//					initialDistance = 0;
//				}
//				//set current coordinate as start
//				start = coord;
//			}
//		}
//		//return new list
//		Coordinate[] coordinates = list.toArray(new Coordinate[list.size()]);
//		GeometryFactory factory = new GeometryFactory();
//		return factory.createLineString(coordinates);
//	}
//
//	private Coordinate splitLine(Coordinate start, Coordinate end, double distance) {
//		//get ratio interval / total length
//		double ratio = distance / start.distance(end);
//		//create coordinate based on the ratio
//		return new Coordinate(start.x + ratio * (end.x - start.x), start.y + ratio * (end.y - start.y));
//	}
//
//	/**
//	 * calculate sinuosity from geometry
//	 * @param geometry  input geometrx
//	 * @return sinuosity
//	 */
//	private double[] calculateSinuosity(Geometry sampledGeometry, int length, int elementLength){
//		if(length % 2 != 0)
//			throw new IllegalArgumentException();
//		//get coordinates
//		Coordinate[] coords = sampledGeometry.getCoordinates();
//		int halfLength = length / 2;
//		double[] sinuosities = new double[coords.length];
//		for(int i=halfLength; i<coords.length - halfLength; i++){
//			//calculate sinuosity
//			double basis = coords[i-halfLength].distance(coords[i+halfLength]);
//			sinuosities[i] = (length * elementLength) / basis;
//		}
//		return sinuosities;
//	}
//
//
//}
