package de.tudresden.gis.fusion.operation.enhancement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class LineIntersection extends AOperation {
	
	private final String IN_FEATURES = "IN_FEATURES";
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.ENHANCEMENT.resource(),
			EProcessType.OP_ENH_GEOM_REP.resource()
	};
	
	@Override
	public void execute() throws ProcessException {
		//get inputs
		IFeatureCollection inFeatures = (IFeatureCollection) getInput(IN_FEATURES);		
		//segmentation
		IFeatureCollection outFeatures = null;
		try {
			if(inFeatures instanceof GTFeatureCollection)
				outFeatures = runIntersection((GTFeatureCollection) inFeatures);
		} catch (IOException ioe) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, ioe);
		}
		//return
		setOutput(OUT_FEATURES, outFeatures);		
	}
	
	/**
	 * computes intersections within a line network
	 * @param inFeatures input line features
	 * @return intersected line features
	 * @throws IOException
	 */
	private IFeatureCollection runIntersection(GTFeatureCollection inFeatures) throws IOException {
		//build index
		GTIndexedFeatureCollection fc = new GTIndexedFeatureCollection(inFeatures);
		//init new collection
		List<SimpleFeature> nFeatures = new ArrayList<SimpleFeature>();						
		//run intersections
	    for(IFeature feature : inFeatures) {
	    	nFeatures.addAll(runIntersection((GTFeature) feature, fc));
		}			    
		//return
		return new GTFeatureCollection(new IRI(inFeatures.getCollectionId()), nFeatures, inFeatures.getDescription());
	}

	/**
	 * computes intersections for specified feature
	 * @param feature input line feature
	 * @param fc intersection features
	 * @return intersected line features
	 */
	private List<SimpleFeature> runIntersection(GTFeature feature, GTIndexedFeatureCollection fc) {
		//get potential intersections
		List<IFeature> pIntersects = fc.boundsIntersect(feature);
		//get intersections
		return runIntersection(feature, pIntersects);
	}

	/**
	 * computes intersections for input feature
	 * @param feature input feature
	 * @param pIntersects possibly intersecting features
	 * @return intersected line features
	 */
	private List<SimpleFeature> runIntersection(GTFeature feature, List<IFeature> pIntersects) {
		List<SimpleFeature> sfCollection = new ArrayList<SimpleFeature>();
		//get linestring and real intersections
		LineString refLine = getLinestring(feature);
		List<LineString> intersectingLines = new ArrayList<LineString>();
		for(IFeature pIntersect : pIntersects){
			LineString tarLine = getLinestring(pIntersect);
			//continue, if lines do not intersect or lines are equal
			if(!tarLine.intersects(refLine) || tarLine.equals(refLine))
				continue;
			intersectingLines.add(tarLine);
		}
		//check if intersections are present
		if(intersectingLines.size() == 0){
			sfCollection.add(feature.getFeature());
			return sfCollection;
		}
		//intersect feature
		List<LineString> nGeometries = runIntersection(refLine, intersectingLines);
		//build new features
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(feature.getFeature().getFeatureType());
		//get feature id
		String fid = feature.getFeature().getID();
		//iterate segments and build new features
		int i = 0;
		for(Geometry geom : nGeometries){
			sfBuilder.init(feature.getFeature());
			sfBuilder.set(feature.getFeature().getDefaultGeometryProperty().getName(), geom);
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
	private LineString getLinestring(IFeature feature) {
		//get default geometry from feature
		Geometry geom = (Geometry) feature.getDefaultSpatialProperty().getValue();
		//get linestring
		if(feature.getDefaultSpatialProperty().getGeometryType().equals(EGeometryType.GML3_1D_CURVE))
			return (LineString) geom;
		else if(feature.getDefaultSpatialProperty().getGeometryType().equals(EGeometryType.GML3_1D_MULTICURVE) && ((MultiLineString) geom).getNumGeometries() == 1)
			return (LineString) ((MultiLineString) geom).getGeometryN(0);
		else
			return null;
	}

	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	protected String getProcessAbstract() {
		return "Topology repair; builds valid geometries with intersections";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_FEATURES, "Input features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.GEOMETRY_LINE.getRestriction()
					}
			)
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					OUT_FEATURES, "Output features",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.GEOMETRY_LINE.getRestriction()
					}
			)
		};
	}

}
