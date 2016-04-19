package de.tudresden.gis.fusion.operation.enhancement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class MultiToSinglepart extends AOperationInstance {
	
	private final String IN_FEATURES = "IN_FEATURES";
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	@Override
	public void execute() throws ProcessException {
		
		//get input
		GTFeatureCollection inFeatures = (GTFeatureCollection) input(IN_FEATURES);
		
		//intersect
		GTFeatureCollection outFeatures = multiToSingle(inFeatures);
		
		//return
		setOutput(OUT_FEATURES, outFeatures);
	}
	
	/**
	 * computes intersections within a line network
	 * @param inFeatures input line features
	 * @return intersected line features
	 * @throws IOException
	 */
	private GTFeatureCollection multiToSingle(GTFeatureCollection inFeatures) {
		//init new collection
		List<SimpleFeature> nFeatures = new ArrayList<SimpleFeature>();						
		//run intersections
	    for(GTFeature feature : inFeatures) {
	    	if(isMultiGeometry(feature))
	    		nFeatures.addAll(multiToSingle((SimpleFeature) feature.resolve()));
	    	else
	    		nFeatures.add((SimpleFeature) feature.resolve());
		}			    
		//return
		return new GTFeatureCollection(inFeatures.asString(), DataUtilities.collection(nFeatures), inFeatures.getDescription());
	}

	/**
	 * transform multi to single geometry
	 * @param feature input feature with multi-geometry
	 * @return set of single-geometries
	 */
	private Collection<? extends SimpleFeature> multiToSingle(SimpleFeature feature) {
		
		List<SimpleFeature> sfCollection = new ArrayList<SimpleFeature>();
		//get geometry
		GeometryCollection geom = (GeometryCollection) feature.getDefaultGeometryProperty().getValue();
		//build new features
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(feature.getFeatureType());
		//get feature id
		String fid = ((SimpleFeature) feature).getID();
		//iterate and build new single part features
		for(int i=0; i<geom.getNumGeometries(); i++){
			sfBuilder.init((SimpleFeature) feature);
			sfBuilder.set(feature.getDefaultGeometryProperty().getName(), geom.getGeometryN(i));
			sfCollection.add(sfBuilder.buildFeature(fid + "_" + i++));
		}
		return sfCollection;
	}
	
	/**
	 * check, if feature has multi-geometry
	 * @param feature input feature
	 * @return true, if multi-geometry
	 */
	private boolean isMultiGeometry(GTFeature feature) {
		//get default geometry from feature
		Geometry geom = (Geometry) feature.resolve().getDefaultGeometryProperty().getValue();
		//check, if number of geometries > 1
		return (geom instanceof GeometryCollection);
	}

	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Multi to Singlepart";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Converts Multi- to Single-part features";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}
}
