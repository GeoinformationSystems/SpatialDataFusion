package de.tud.fusion.operation.enhancement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;

import de.tud.fusion.data.feature.AbstractFeature;
import de.tud.fusion.data.feature.FeatureCollection;
import de.tud.fusion.data.feature.IndexedFeatureCollection;
import de.tud.fusion.data.feature.geotools.GTFeatureCollection;
import de.tud.fusion.operation.AbstractOperation;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.InputConnector;
import de.tud.fusion.operation.description.OutputConnector;

public class LineIntersection extends AbstractOperation {
	
	public final static String PROCESS_ID = LineIntersection.class.getSimpleName();
	
	private final String IN_FEATURES = "IN_FEATURES";
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private Set<IInputConnector> inputConnectors;
	private Set<IOutputConnector> outputConnectors;
	
	/**
	 * constructor
	 */
	public LineIntersection() {
		super(PROCESS_ID);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector featureConnector = getInputConnector(IN_FEATURES);
		//get input
		@SuppressWarnings("unchecked")
		FeatureCollection<? extends AbstractFeature> features = (FeatureCollection<? extends AbstractFeature>) featureConnector.getData();
		//intersect
		features = runIntersection(features);
		//set output connector
		setOutputConnector(OUT_FEATURES, features);
	}
	
	/**
	 * computes intersections within a line network
	 * @param inFeatures input line features
	 * @return intersected line features
	 * @throws IOException
	 */
	private <T extends AbstractFeature> FeatureCollection<?> runIntersection(FeatureCollection<T> inFeatures) {
		//build index
		IndexedFeatureCollection<T> fc = new IndexedFeatureCollection<T>(inFeatures.getIdentifier(), inFeatures.resolve(), inFeatures.getDescription());
		//init new collection
		List<SimpleFeature> nFeatures = new ArrayList<SimpleFeature>();						
		//run intersections
	    for(T feature : inFeatures) {
	    	nFeatures.addAll(runIntersection(feature, fc));
		}			    
		//return
		return new GTFeatureCollection(inFeatures.getIdentifier(), DataUtilities.collection(nFeatures), inFeatures.getDescription());
	}

	/**
	 * computes intersections for specified feature
	 * @param feature input line feature
	 * @param fc intersection features
	 * @return intersected line features
	 */
	private <T extends AbstractFeature> List<SimpleFeature> runIntersection(T feature, IndexedFeatureCollection<T> fc) {
		//get potential intersections
		List<T> pIntersects = fc.boundsIntersect(feature.getRepresentation().resolve());
		//get intersections
		return runIntersection((SimpleFeature) feature.getRepresentation().resolve(), pIntersects);
	}

	/**
	 * computes intersections for input feature
	 * @param feature input feature
	 * @param pIntersects possibly intersecting features
	 * @return intersected line features
	 */
	private <T extends AbstractFeature> List<SimpleFeature> runIntersection(SimpleFeature feature, List<T> pIntersects) {
		List<SimpleFeature> sfCollection = new ArrayList<SimpleFeature>();
		//get linestring and real intersections
		LineString refLine = getLinestring(feature);
		List<LineString> intersectingLines = new ArrayList<LineString>();
		for(T pIntersect : pIntersects){
			LineString tarLine = getLinestring(pIntersect.getRepresentation().resolve());
			//continue, if lines do not intersect or lines are equal
			if(!tarLine.intersects(refLine) || tarLine.equals(refLine))
				continue;
			intersectingLines.add(tarLine);
		}
		//check if intersections are present
		if(intersectingLines.size() == 0){
			sfCollection.add(feature);
			return sfCollection;
		}
		//intersect feature
		List<LineString> nGeometries = runIntersection(refLine, intersectingLines);
		//build new features
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(feature.getFeatureType());
		//get feature id
		String fid = ((SimpleFeature) feature).getID();
		//iterate segments and build new features
		int i = 0;
		for(Geometry geom : nGeometries){
			sfBuilder.init((SimpleFeature) feature);
			sfBuilder.set(feature.getDefaultGeometryProperty().getName(), geom);
			sfCollection.add(sfBuilder.buildFeature(fid + "_" + i++));
		}
		return sfCollection;
	}

	/**
	 * computes line intersection
	 * @param refLine input line
	 * @param intersectingLines intersecting lines
	 * @return intersected lines
	 */
	private List<LineString> runIntersection(LineString refLine, List<LineString> intersectingLines) {
		//get intersection points
		CoordinateList intersections = new CoordinateList();
		for(LineString line : intersectingLines){
			intersections.addAll(Arrays.asList(refLine.intersection(line).getCoordinates()), false);
		}
		//split line
		return splitLine(refLine, intersections);
	}

	/**
	 * computes line intersection
	 * @param refLine input line
	 * @param intersections intersection points
	 * @return line intersected at intersection points
	 */
	private List<LineString> splitLine(LineString refLine, CoordinateList intersections) {
		List<LineString> geometries = new ArrayList<LineString>();
		GeometryFactory factory = new GeometryFactory();
		//iterate coordinate array
		Coordinate[] refCoords = refLine.getCoordinates();
		CoordinateList coordList = new CoordinateList();
		for(int i=0; i<refCoords.length-1; i++){
			//add start point to line
			coordList.add(refCoords[i], false);
			//check if points intersect current segment
			Collection<Coordinate> pointsOnLine = pointsOnLine(refCoords[i], refCoords[i+1], intersections);
			for(Coordinate coord : pointsOnLine){
				//add intersection point
				coordList.add(coord, false);
				//add new line; clear list and add intersection point
				if(coordList.size() > 1){
					geometries.add(factory.createLineString(coordList.toCoordinateArray()));
					coordList.clear();
					coordList.add(coord, false);
				}
			}
		}
		//add final part of line (if not yet set)
		coordList.add(refCoords[refCoords.length-1], false);
		if(coordList.size() > 1)
			geometries.add(factory.createLineString(coordList.toCoordinateArray()));
		
		return geometries;
	}

	/**
	 * selection of points between start and end (assumes points on line between start and end)
	 * @param start start point
	 * @param end end point
	 * @param intersections possible intersections
	 * @return intersections between start and end
	 */
	private Collection<Coordinate> pointsOnLine(Coordinate start, Coordinate end, CoordinateList intersections) {
		//iterate intersections and put into map (sorted by distance to start coordinate)
		SortedMap<Double,Coordinate> coordMap = new TreeMap<Double,Coordinate>();
		for(Coordinate coord : intersections.toCoordinateArray()){
			if(pointOnLine(start, end, coord)){
				coordMap.put(start.distance(coord), coord);
			}
		}		
		return coordMap.values();
	}

	/**
	 * check if point is on line
	 * @param start start point
	 * @param end end point
	 * @param coord input coordinate to check
	 * @return true, if coord is located between start and end
	 */
	private boolean pointOnLine(Coordinate start, Coordinate end, Coordinate coord) {
		//check if point is between start and end
		return new Envelope(start, end).contains(coord);
	}

	/**
	 * get linestring geometry from feature
	 * @param geometry input geometry
	 * @return linestring geometry
	 */
	private LineString getLinestring(Feature feature) {
		//get default geometry from feature
		Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
		//get linestring
		if(geom instanceof LineString)
			return (LineString) geom;
		else if(geom instanceof MultiLineString && ((MultiLineString) geom).getNumGeometries() == 1)
			return (LineString) ((MultiLineString) geom).getGeometryN(0);
		else if(geom instanceof Polygon)
			//TODO: handle polygons
			return ((Polygon) geom).getExteriorRing();
		else
			throw new IllegalArgumentException("Feature is not a LineString");
	}
	
	@Override
	public Set<IInputConnector> getInputConnectors() {
		if(inputConnectors != null)
			return inputConnectors;
		//generate descriptions
		inputConnectors = new HashSet<IInputConnector>();
		inputConnectors.add(new InputConnector(
				IN_FEATURES, IN_FEATURES, "Input features",
				new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(FeatureCollection.class)},
				null,
				null));
		//return
		return inputConnectors;
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		if(outputConnectors != null)
			return outputConnectors;
		//generate descriptions
		outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.add(new OutputConnector(
				OUT_FEATURES, OUT_FEATURES, "Intersected features",
				new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(FeatureCollection.class)},
				null));		
		//return
		return outputConnectors;
	}

	@Override
	public String getProcessTitle() {
		return "Line intersection";
	}

	@Override
	public String getProcessAbstract() {
		return "Intersects linear network to avoid topological inconsistency";
	}
}
