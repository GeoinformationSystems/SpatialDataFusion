package de.tudresden.gis.fusion.operation.enhancement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class LineIntersection extends AOperationInstance {
	
	private final String IN_FEATURES = "IN_FEATURES";
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	@Override
	public void execute() throws ProcessException {
		
		//get input
		GTFeatureCollection inFeatures = (GTFeatureCollection) getInput(IN_FEATURES);
		
		//intersect
		GTFeatureCollection outFeatures = runIntersection(inFeatures);
		
		//return
		setOutput(OUT_FEATURES, outFeatures);
	}
	
	/**
	 * computes intersections within a line network
	 * @param inFeatures input line features
	 * @return intersected line features
	 * @throws IOException
	 */
	private GTFeatureCollection runIntersection(GTFeatureCollection inFeatures) {
		//build index
		GTIndexedFeatureCollection fc = new GTIndexedFeatureCollection(inFeatures.getIdentifier(), inFeatures.collection());
		//init new collection
		List<SimpleFeature> nFeatures = new ArrayList<SimpleFeature>();						
		//run intersections
	    for(GTFeature feature : inFeatures) {
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
	private List<SimpleFeature> runIntersection(GTFeature feature, GTIndexedFeatureCollection fc) {
		//get potential intersections
		List<Feature> pIntersects = fc.boundsIntersect(feature.resolve());
		//get intersections
		return runIntersection((SimpleFeature) feature.resolve(), pIntersects);
	}

	/**
	 * computes intersections for input feature
	 * @param feature input feature
	 * @param pIntersects possibly intersecting features
	 * @return intersected line features
	 */
	private List<SimpleFeature> runIntersection(SimpleFeature feature, List<Feature> pIntersects) {
		List<SimpleFeature> sfCollection = new ArrayList<SimpleFeature>();
		//get linestring and real intersections
		LineString refLine = getLinestring(feature);
		List<LineString> intersectingLines = new ArrayList<LineString>();
		for(Feature pIntersect : pIntersects){
			LineString tarLine = getLinestring(pIntersect);
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
		else
			throw new IllegalArgumentException("Feature is not a LineString");
	}

	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Line intersection";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Intersects linear network to avoid topological inconsistency";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		if(inputDescriptions == null){
			inputDescriptions = new HashSet<IInputDescription>();
			inputDescriptions.add(new InputDescription(IN_FEATURES, IN_FEATURES, "Input features)",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_FEATURES),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
		}
		return inputDescriptions;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		if(outputDescriptions == null){
			outputDescriptions = new HashSet<IOutputDescription>();
			outputDescriptions.add(new OutputDescription(
					OUT_FEATURES, OUT_FEATURES, "Intersected features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_FEATURES),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
		}
		return outputDescriptions;
	}
}
