package de.tudresden.geoinfo.fusion.operation.harmonization;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRSReproject extends AbstractOperation {

    private static final IIdentifier PROCESS = new Identifier(CRSReproject.class.getSimpleName());

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_SOURCE");
	private final static IIdentifier IN_RANGE = new Identifier("IN_TARGET");
	private final static IIdentifier IN_CRS = new Identifier("IN_CRS");

	private final static IIdentifier OUT_DOMAIN = new Identifier("OUT_SOURCE");
	private final static IIdentifier OUT_RANGE = new Identifier("OUT_TARGET");

	/**
	 * constructor
	 */
	public CRSReproject() {
		super(PROCESS);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector domainConnector = getInputConnector(IN_DOMAIN);
        IInputConnector rangeConnector = getInputConnector(IN_RANGE);
        IInputConnector crsConnector = getInputConnector(IN_CRS);
        //validate input
        if(!rangeConnector.isConnected() && !crsConnector.isConnected())
            throw new IllegalArgumentException("Either IN_TARGET or IN_CRS must be set as input");
		//get features
        GTFeatureCollection domainFeatures = (GTFeatureCollection) domainConnector.getData();
        GTFeatureCollection rangeFeatures = rangeConnector.isConnected() ? (GTFeatureCollection) rangeConnector.getData() : null;
        //get crs
        CoordinateReferenceSystem domainCRS = getCRS(domainFeatures);
        CoordinateReferenceSystem rangeCRS = rangeFeatures != null ? getCRS(rangeFeatures) : null;
        //get final crs
        CoordinateReferenceSystem crsFinal = crsConnector.isConnected() ? decodeCRS((URILiteral) crsConnector.getData()) : rangeCRS;
        //transform
        domainFeatures = reproject(domainFeatures, domainCRS, crsFinal);
        if(rangeFeatures != null)
            rangeFeatures = crsConnector.isConnected() ? reproject(rangeFeatures, domainCRS, crsFinal) : rangeFeatures;
		//set output connector
		connectOutput(OUT_DOMAIN, domainFeatures);
        if(rangeFeatures != null)
            connectOutput(OUT_RANGE, rangeFeatures);
	}

    /**
     * decode CRS URI
     * @param literal URI literal
     * @return decodes CRS
     */
    private CoordinateReferenceSystem decodeCRS(URILiteral literal){
        return decodeCRS(literal.getValue());
    }

    /**
     * decode CRS String
     * @param identifier CRS String
     * @return decodes CRS
     */
    private CoordinateReferenceSystem decodeCRS(String identifier){
        try {
            return CRS.decode(identifier);
        } catch (FactoryException e1) {
            throw new RuntimeException("final crs cannot be resolved");
        }
    }

    /**
     * get crs from feature collection
     * @param features input collection
     * @return crs of the input features
     */
    private CoordinateReferenceSystem getCRS(GTFeatureCollection features){
        //return crs of first feature
        return ((SimpleFeature) features.iterator().next().resolve()).getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
    }

    /**
     * reproject feature collection
     * @param features input features to be reprojected
     * @param featureCRS crs of the inputs
     * @param finalCRS target crs
     * @return reprojected features
     */
    private GTFeatureCollection reproject(GTFeatureCollection features, CoordinateReferenceSystem featureCRS, CoordinateReferenceSystem finalCRS) {
        //check if feature colletion is set
        if(features == null || features.size() == 0)
            return null;
        //check if transformation is required/applicable
        if(featureCRS == null || finalCRS == null || featureCRS.equals(finalCRS))
            return features;
        //init new collection
        List<SimpleFeature> features_proj = new ArrayList<>();
        //get transformation
        MathTransform transformation = getTransformation(featureCRS, finalCRS);
        //iterate collection and transform
        for(GTVectorFeature feature : features) {
            features_proj.add(reproject(((GTVectorRepresentation) feature.getRepresentation()).resolve(), featureCRS, finalCRS, transformation));
        }
        //return
        return new GTFeatureCollection(features.getIdentifier(), DataUtilities.collection(features_proj), features.getMetadata());
    }

    /**
     * set or transform crs for simple feature
     * @param feature input feature
     * @param rangeCRS target reference system
     * @param transformation crs transformation
     * @return transformed feature
     */
    private SimpleFeature reproject(SimpleFeature feature, CoordinateReferenceSystem domainCRS, CoordinateReferenceSystem rangeCRS, MathTransform transformation) {
        //return feature if sourceCRS = rangeCRS or one of the crs is null
        if(domainCRS == null || rangeCRS == null || domainCRS.equals(rangeCRS))
            return feature;
        //build new featuretype
        SimpleFeatureTypeBuilder ftBuilder= new SimpleFeatureTypeBuilder();
        //get input type
        SimpleFeatureType inputType = feature.getFeatureType();
        //configure ft builder
        ftBuilder.setName(inputType.getName());
        for(AttributeDescriptor desc : inputType.getAttributeDescriptors()){
            //set crs for geometry types
            if(desc instanceof GeometryDescriptor){
                AttributeTypeBuilder aBuilder = new AttributeTypeBuilder();
                aBuilder.init(desc);
                aBuilder.setCRS(rangeCRS);
                aBuilder.setBinding(desc.getType().getBinding());
                ftBuilder.add(aBuilder.buildDescriptor(desc.getName(), aBuilder.buildGeometryType()));
            }
            //add non-geometry attribute
            else
                ftBuilder.add(desc);
        }
        //set default geometry name
        ftBuilder.setDefaultGeometry(inputType.getGeometryDescriptor().getLocalName());

        //build new feature
        SimpleFeatureType newType = ftBuilder.buildFeatureType();
        SimpleFeatureBuilder fBuilder= new SimpleFeatureBuilder(newType);

        for(Property attribute : feature.getProperties()){
            //transform geometry attribute if required
            if(attribute instanceof GeometryAttribute){
                GeometryAttribute geomAttribute = (GeometryAttribute) attribute;
                Geometry geometrySource = (Geometry) geomAttribute.getValue();
                Geometry geometryTarget = reprojectGeometry(geometrySource, transformation);
                geomAttribute.setValue(geometryTarget);
                fBuilder.set(attribute.getName(), geomAttribute.getValue());
            }
            //add non-geometry feature attribute
            else
                fBuilder.set(attribute.getName(), attribute.getValue());
        }

        //return new feature
        return fBuilder.buildFeature(feature.getID());
    }

    /**
     * transform geometry
     * @param geom input geometry
     * @return transformed geometry
     */
    private Geometry reprojectGeometry(Geometry geom, MathTransform transformation) {
        if(geom == null) return null;
        try {
            return JTS.transform(geom, transformation);
        } catch (Exception e) {
            throw new RuntimeException("Could not reproject geometry", e);
        }
    }

    /**
     * get transformation
     * @param sourceCRS input crs
     * @param targetCRS output crs
     * @return transformation
     */
    private MathTransform getTransformation(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
        try {
            return CRS.findMathTransform(sourceCRS, targetCRS);
        } catch (FactoryException fe) {
            try {
                sourceCRS = decodeCRSDetails(sourceCRS);
                targetCRS = decodeCRSDetails(targetCRS);
                return CRS.findMathTransform(sourceCRS, targetCRS);
            } catch (FactoryException e) {
                throw new RuntimeException("Could not determine CRS transformation", e);
            }
        }
    }

    /**
     * identifies details for an input CRS
     * @param inCRS input CRS
     * @return output CRS with details
     * @throws FactoryException
     */
    private CoordinateReferenceSystem decodeCRSDetails(CoordinateReferenceSystem inCRS) throws FactoryException {
        String identifier = CRS.lookupIdentifier(inCRS, true);
        CoordinateReferenceSystem outCRS = CRS.decode(identifier);
        //check axis order, if they don't match, put longitude first (WGS84 issue)
        if(CRS.getAxisOrder(inCRS) != CRS.getAxisOrder(outCRS))
            outCRS = CRS.decode(identifier, true);
        //recheck axis order
        if(CRS.getAxisOrder(inCRS) != CRS.getAxisOrder(outCRS))
            throw new RuntimeException("CRS decoding resulted in incorrect axis order");
        return outCRS;
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_DOMAIN, new InputConnector(
                IN_DOMAIN,
                new MetadataForConnector(IN_DOMAIN.toString(), "Domain features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_RANGE, new InputConnector(
                IN_RANGE,
                new MetadataForConnector(IN_RANGE.toString(), "Range features (mandatory, if IN_CRS is null)"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class)},
                null,
                null));
        inputConnectors.put(IN_CRS, new InputConnector(
                IN_CRS,
                new MetadataForConnector(IN_CRS.toString(), "Input coordinate reference system (mandatory, if IN_TARGET is null)"),
                new IDataConstraint[]{
                        new BindingConstraint(URILiteral.class)},
                null,
                null));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_DOMAIN, new OutputConnector(
                OUT_DOMAIN,
                new MetadataForConnector(OUT_DOMAIN.toString(), "Transformed domain features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null));
        outputConnectors.put(OUT_RANGE, new OutputConnector(
                OUT_RANGE,
                new MetadataForConnector(OUT_RANGE.toString(), "Transformed range features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class)},
                null));
        return outputConnectors;
    }

	@Override
	public String getProcessTitle() {
		return "CRS reproject";
	}

	@Override
	public String getProcessAbstract() {
		return "Reprojects coordinate reference system of the input features";
	}
}
