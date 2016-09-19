package de.tud.fusion.operation.enhancement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.primitive.Curve;

import de.tud.fusion.data.feature.AbstractFeature;
import de.tud.fusion.data.feature.FeatureCollection;
import de.tud.fusion.data.feature.IndexedFeatureCollection;
import de.tud.fusion.data.feature.geotools.GTFeature;
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
	private <T extends AbstractFeature> FeatureCollection<GTFeature> runIntersection(FeatureCollection<T> inFeatures) {
		//build index
		IndexedFeatureCollection<T> fc = new IndexedFeatureCollection<T>(inFeatures.getIdentifier(), inFeatures.resolve(), inFeatures.getDescription());
		//init new collection
		List<GTFeature> nFeatures = new ArrayList<GTFeature>();						
		//run intersections
	    for(T feature : inFeatures) {
	    	nFeatures.addAll(runIntersection(feature, fc));
		}			    
		//return
		return new IndexedFeatureCollection<GTFeature>(inFeatures.getIdentifier(), nFeatures, inFeatures.getDescription());
	}

	/**
	 * computes intersections for specified feature
	 * @param feature input line feature
	 * @param fc intersection features
	 * @return intersected line features
	 */
	private <T extends AbstractFeature> List<GTFeature> runIntersection(T feature, IndexedFeatureCollection<T> fc) {
		//get potential intersections
		List<Feature> pIntersects = fc.boundsIntersect((Feature) feature.getRepresentation().resolve());
		//get intersections
		return runIntersection(feature, pIntersects);
	}

	/**
	 * computes intersections for input feature
	 * @param feature input feature
	 * @param pIntersects possibly intersecting features
	 * @return intersected line features
	 */
	private List<GTFeature> runIntersection(AbstractFeature feature, List<Feature> pIntersects) {
		List<GTFeature> sfCollection = new ArrayList<GTFeature>();
		//get linestring and real intersections
		Curve refLine = getCurve((Feature) feature.getRepresentation().resolve());
		List<Curve> intersectingLines = new ArrayList<Curve>();
		for(Feature pIntersect : pIntersects){
			Curve tarLine = getCurve(pIntersect);
			//continue, if lines do not intersect or lines are equal
			if(!tarLine.intersects(refLine) || tarLine.equals(refLine))
				continue;
			intersectingLines.add(tarLine);
		}
		//check if intersections are present
		if(intersectingLines.size() == 0){
			sfCollection.add(new GTFeature(feature));
			return sfCollection;
		}
		//intersect feature
		List<Curve> nGeometries = runIntersection(refLine, intersectingLines);
		//build new features
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder((SimpleFeatureType) feature.getType().resolve());
		//get feature id
		String fid = ((SimpleFeature) feature).getID();
		//iterate segments and build new features
		int i = 0;
		for(Geometry geom : nGeometries){
			sfBuilder.init((SimpleFeature) feature);
			sfBuilder.set(((Feature) feature.getRepresentation().resolve()).getDefaultGeometryProperty().getName(), geom);
			sfCollection.add(new GTFeature(feature.getIdentifier(), sfBuilder.buildFeature(fid + "_" + i++), feature.getDescription(), feature.getRelations()));
		}
		return sfCollection;
	}

	/**
	 * computes line intersection
	 * @param refLine input line
	 * @param intersectingLines intersecting lines
	 * @return intersected lines
	 */
	private List<Curve> runIntersection(Curve refLine, List<Curve> intersectingCurves) {
		//get intersection points
		TransfiniteSet intersections = null;
		for(Curve curve : intersectingCurves){
			if(intersections == null)
				intersections = refLine.intersection(curve);
			else
				intersections = intersections.union(refLine.intersection(curve));
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
	private List<Curve> splitLine(Curve refLine, TransfiniteSet intersections) {
		return null;
//		List<LineString> geometries = new ArrayList<LineString>();
//		GeometryFactory factory = new GeometryFactory();
//		//iterate coordinate array
//		Coordinate[] refCoords = refLine.getCoordinates();
//		CoordinateList coordList = new CoordinateList();
//		for(int i=0; i<refCoords.length-1; i++){
//			//add start point to line
//			coordList.add(refCoords[i], false);
//			//check if points intersect current segment
//			Collection<Coordinate> pointsOnLine = pointsOnLine(refCoords[i], refCoords[i+1], intersections);
//			for(Coordinate coord : pointsOnLine){
//				//add intersection point
//				coordList.add(coord, false);
//				//add new line; clear list and add intersection point
//				if(coordList.size() > 1){
//					geometries.add(factory.createLineString(coordList.toCoordinateArray()));
//					coordList.clear();
//					coordList.add(coord, false);
//				}
//			}
//		}
//		//add final part of line (if not yet set)
//		coordList.add(refCoords[refCoords.length-1], false);
//		if(coordList.size() > 1)
//			geometries.add(factory.createLineString(coordList.toCoordinateArray()));
//		
//		return geometries;
	}

	/**
	 * selection of points between start and end (assumes points on line between start and end)
	 * @param start start point
	 * @param end end point
	 * @param intersections possible intersections
	 * @return intersections between start and end
	 */
//	private Collection<Coordinate> pointsOnLine(Coordinate start, Coordinate end, CoordinateList intersections) {
//		//iterate intersections and put into map (sorted by distance to start coordinate)
//		SortedMap<Double,Coordinate> coordMap = new TreeMap<Double,Coordinate>();
//		for(Coordinate coord : intersections.toCoordinateArray()){
//			if(pointOnLine(start, end, coord)){
//				coordMap.put(start.distance(coord), coord);
//			}
//		}		
//		return coordMap.values();
//	}

	/**
	 * check if point is on line
	 * @param start start point
	 * @param end end point
	 * @param coord input coordinate to check
	 * @return true, if coord is located between start and end
	 */
//	private boolean pointOnLine(Coordinate start, Coordinate end, Coordinate coord) {
//		//check if point is between start and end
//		return new Envelope(start, end).contains(coord);
//	}

	/**
	 * get curve geometry from feature
	 * @param geometry input geometry
	 * @return curve geometry
	 */
	private Curve getCurve(Feature feature) {
		//get default geometry from feature
		System.out.println(feature.getDefaultGeometryProperty());
		Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
		//get linestring
		if(geom instanceof Curve)
			return (Curve) geom;
		else if(geom instanceof MultiCurve && ((MultiCurve) geom).getElements().size() == 1)
			return (Curve) ((MultiCurve) geom).getElements().iterator().next();
		else
			throw new IllegalArgumentException("Feature is not a curve");
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
						new BindingConstraint(FeatureCollection.class, true)},
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
						new BindingConstraint(FeatureCollection.class, false)},
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
