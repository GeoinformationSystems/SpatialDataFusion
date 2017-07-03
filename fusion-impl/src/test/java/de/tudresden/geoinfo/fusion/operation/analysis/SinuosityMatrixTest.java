package de.tudresden.geoinfo.fusion.operation.analysis;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SinuosityMatrixTest extends AbstractTest {

    private final static String IN_FEATURE = "IN_FEATURE";
    private final static String IN_INTERVAL = "IN_INTERVAL";
    private final static String IN_PLOT = "IN_PLOT";
    private final static String OUT_MATRIX = "OUT_MATRIX";
    private final static String OUT_PLOT = "OUT_PLOT";

	@Test
	public void createSinuosityMatrix() throws IOException {
        createSinuosityMatrix(
                ShapefileParser.readShapefile(new File("src/test/resources/rivers_sn.shp").toURI().toURL(), true),
                "rivers_sn.55",
                new DecimalLiteral(100),
                false);
    }

    @Test
    public void createSinuosityMatrixWithPlot() throws IOException {
        createSinuosityMatrix(
                ShapefileParser.readShapefile(new File("src/test/resources/rivers_sn.shp").toURI().toURL(), true),
                "rivers_sn.55",
                null,
                true);
    }

    private void createSinuosityMatrix(GTFeatureCollection features, String featureId, @Nullable DecimalLiteral interval, boolean plot) {
        GTVectorFeature feature = features.getFeatureById(featureId);
        if(feature == null)
            throw new IllegalArgumentException("Feature id " + featureId + " does not exist");
	    createSinuosityMatrix(feature, interval, plot);
    }

    private void createSinuosityMatrix(@NotNull GTVectorFeature feature, @Nullable DecimalLiteral interval, boolean plot) {

        AbstractOperation operation = new SinuosityMatrix(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_FEATURE, feature);
        inputs.put(IN_INTERVAL, interval);
        if(plot)
            inputs.put(IN_PLOT, new BooleanLiteral(true));

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_MATRIX, URLLiteral.class);
        if(plot)
            outputs.put(OUT_PLOT, URLLiteral.class);

        this.execute(operation, inputs, outputs);

    }

//		ShapefileParser parser = new ShapefileParser();
//		Map<String,IData> input = new HashMap<String,IData>();
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral(new File("D:/Geodaten/LfULG/Gew�sserstrukturkartierung", "Gewaessernetz.shp").toURI()));
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


}
