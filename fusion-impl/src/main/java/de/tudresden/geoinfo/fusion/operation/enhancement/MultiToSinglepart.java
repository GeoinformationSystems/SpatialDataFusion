package de.tudresden.geoinfo.fusion.operation.enhancement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.*;

public class MultiToSinglepart extends AbstractOperation {

	private static final IIdentifier PROCESS = new Identifier(MultiToSinglepart.class.getSimpleName());

    private final static IIdentifier IN_FEATURES = new Identifier("IN_FEATURES");
    private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");

    /**
     * constructor
     */
    public MultiToSinglepart() {
        super(PROCESS);
    }

	@Override
	public void execute() {
        //get input connectors
        IInputConnector featureConnector = getInputConnector(IN_FEATURES);
        //get input
        GTFeatureCollection features = (GTFeatureCollection) featureConnector.getData();
        //intersect
        features = multiToSingle(features);
        //set output connector
        connectOutput(OUT_FEATURES, features);
	}

	/**
	 * computes intersections within a line network
	 * @param inFeatures input line features
	 * @return intersected line features
	 * @throws IOException
	 */
	private GTFeatureCollection multiToSingle(GTFeatureCollection inFeatures) {
		//init new collection
		List<SimpleFeature> nFeatures = new ArrayList<>();
		//run intersections
	    for(GTVectorFeature feature : inFeatures) {
	    	if(isMultiGeometry(feature))
	    		nFeatures.addAll(multiToSingle((SimpleFeature) feature.resolve()));
	    	else
	    		nFeatures.add((SimpleFeature) feature.resolve());
		}
		//return
		return new GTFeatureCollection(inFeatures.getIdentifier(), DataUtilities.collection(nFeatures), inFeatures.getMetadata());
	}

	/**
	 * transform multi to single geometry
	 * @param feature input feature with multi-geometry
	 * @return set of single-geometries
	 */
	private Collection<? extends SimpleFeature> multiToSingle(SimpleFeature feature) {
		List<SimpleFeature> sfCollection = new ArrayList<>();
		//get geometry
		GeometryCollection geom = (GeometryCollection) feature.getDefaultGeometryProperty().getValue();
		//build new features
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(feature.getFeatureType());
		//get feature id
		String fid = feature.getID();
		//iterate and build new single part features
		for(int i=0; i<geom.getNumGeometries(); i++){
			sfBuilder.init(feature);
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
	private boolean isMultiGeometry(GTVectorFeature feature) {
		//get default geometry from feature
		Geometry geom = ((GTVectorRepresentation) feature.getRepresentation()).getDefaultGeometry();
		//check, if number of geometries > 1
		return (geom instanceof GeometryCollection);
	}

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_FEATURES, new InputConnector(
                IN_FEATURES,
                new MetadataForConnector(IN_FEATURES.toString(), "Input fetures"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_FEATURES, new OutputConnector(
                OUT_FEATURES,
                new MetadataForConnector(OUT_FEATURES.toString(), "Singlepart features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null));
        return outputConnectors;
    }

    @Override
    public String getProcessTitle() {
        return "Multi to Singlepart";
    }

    @Override
    public String getProcessAbstract() {
        return "Splits multipart features into singlepart features";
    }

}
