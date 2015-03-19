package de.tudresden.gis.fusion.operation.enhancement;

import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
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
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class Segmentation extends AOperation {
	
	private final String IN_FEATURES = "IN_FEATURES";
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.ENHANCEMENT.resource(),
			EProcessType.OP_ENH_GEOM_SEG.resource()
	};
	
	@Override
	public void execute() throws ProcessException {

		//get inputs
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_FEATURES);		
		//segmentation
		IFeatureCollection outFeatures = runSegmentation(inReference);		
		//return
		setOutput(OUT_FEATURES, outFeatures);
		
	}

	/**
	 * segmentation of a feature collection
	 * @param inFeatures input fetaures
	 * @return feature collection with segments
	 */
	private IFeatureCollection runSegmentation(IFeatureCollection inFeatures) {
		
		//check if feature colletion is set
		if(inFeatures == null || inFeatures.size() == 0)
			return null;
		
		//init new collection
		List<SimpleFeature> segFeatures = new ArrayList<SimpleFeature>();
				
		//run segmentation (only GTFeatures are supported)
	    for(IFeature feature : inFeatures) {
	    	if(feature instanceof GTFeature)
	    		segFeatures.addAll(runSegmentation((GTFeature) feature));
		}
	    
		//return
		return new GTFeatureCollection(new IRI(inFeatures.getCollectionId()), segFeatures, inFeatures.getDescription());
	}

	/**
	 * segmentation of a feature
	 * @param feature input feature
	 * @return feature segments
	 */
	private List<SimpleFeature> runSegmentation(GTFeature feature) {
		//get simple feature
		SimpleFeature sf = feature.getFeature();
		List<SimpleFeature> sfCollection = new ArrayList<SimpleFeature>();
		//get feature id
		String fid = sf.getID();
		//split geometry into segments
		List<Geometry> segments = splitGeometry((Geometry) sf.getDefaultGeometry());
		//init builder
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(sf.getFeatureType());
		//iterate segments and build new features
		int i = 0;
		for(Geometry segment : segments){
			sfBuilder.init(sf);
			sfBuilder.set(sf.getDefaultGeometryProperty().getName(), segment);
			sfCollection.add(sfBuilder.buildFeature(fid + "_" + i++));
		}
		return sfCollection;
	}

	/**
	 * split geometry into segments
	 * @param geometry input geometry
	 * @return list of segments
	 */
	private List<Geometry> splitGeometry(Geometry geometry) {
		List<Geometry> segments = new ArrayList<Geometry>();
		GeometryFactory factory = new GeometryFactory();
		//iterate geometries
		int num = geometry.getNumGeometries();
		for(int i=0; i<num; i++){
			Geometry geom = geometry.getGeometryN(i);
			Coordinate[] coords = geom.getCoordinates();
			if(coords.length < 2)
				continue;
			for(int j=0; j<coords.length-1; j++){
				segments.add(factory.createLineString(new Coordinate[]{coords[j], coords[j+1]}));
			}
		}		
		return segments;
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
		return "Segmentation of feature geometries";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_FEATURES, "Input features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.GEOMETRY_NoPOINT.getRestriction()
					}
			)
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					OUT_FEATURES, "Output segments",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.GEOMETRY_NoPOINT.getRestriction()
					}
			)
		};
	}
}
