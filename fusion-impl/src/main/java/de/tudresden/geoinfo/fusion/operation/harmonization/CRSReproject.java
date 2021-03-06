package de.tudresden.geoinfo.fusion.operation.harmonization;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.List;

public class CRSReproject extends AbstractOperation {

    private static final String PROCESS_TITLE = CRSReproject.class.getName();
    private static final String PROCESS_DESCRIPTION = "Reprojects coordinate reference system of the input features";

    private final static String IN_DOMAIN_TITLE = "IN_DOMAIN";
    private final static String IN_DOMAIN_DESCRIPTION = "Domain features";
    private final static String IN_RANGE_TITLE = "IN_RANGE";
    private final static String IN_RANGE_DESCRIPTION = "Range features (mandatory, if IN_CRS is null)";
    private final static String IN_CRS_TITLE = "IN_CRS";
    private final static String IN_CRS_DESCRIPTION = "Input coordinate reference system (mandatory, if IN_RANGE is null)";
    private final static String OUT_DOMAIN_TITLE = "OUT_DOMAIN";
    private final static String OUT_DOMAIN_DESCRIPTION = "Transformed domain features";
    private final static String OUT_RANGE_TITLE = "OUT_RANGE";
    private final static String OUT_RANGE_DESCRIPTION = "Transformed range features";

    /**
     * constructor
     */
    public CRSReproject() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {

        //get input
        GTFeatureCollection domainFeatures = (GTFeatureCollection) this.getMandatoryInputData(IN_DOMAIN_TITLE);
        GTFeatureCollection rangeFeatures = (GTFeatureCollection) this.getInputData(IN_RANGE_TITLE);
        URLLiteral finalCRSURI = (URLLiteral) this.getInputData(IN_CRS_TITLE);

        //validate input
        if (rangeFeatures == null && finalCRSURI == null)
            throw new IllegalArgumentException("Either " + IN_RANGE_TITLE + " or " + IN_CRS_TITLE + " must be set as input");

        //set final crs
        CoordinateReferenceSystem domainCRS = getCRS(domainFeatures);
        CoordinateReferenceSystem rangeCRS = rangeFeatures != null ? getCRS(rangeFeatures) : null;
        CoordinateReferenceSystem finalCRS = finalCRSURI != null ? decodeCRS(finalCRSURI) : rangeCRS;

        //transform
        domainFeatures = reproject(domainFeatures, domainCRS, finalCRS);
        if (rangeFeatures != null)
            rangeFeatures = finalCRSURI != null ? reproject(rangeFeatures, domainCRS, finalCRS) : rangeFeatures;

        //set output
        this.setOutput(OUT_DOMAIN_TITLE, domainFeatures);
        if (rangeFeatures != null)
            setOutput(OUT_RANGE_TITLE, rangeFeatures);

    }

    /**
     * decode CRS URI
     *
     * @param literal URI literal
     * @return decodes CRS
     */
    private CoordinateReferenceSystem decodeCRS(URLLiteral literal) {
        return decodeCRS(literal.getLiteralValue());
    }

    /**
     * decode CRS String
     *
     * @param identifier CRS String
     * @return decodes CRS
     */
    private @NotNull CoordinateReferenceSystem decodeCRS(@NotNull String identifier) {
        try {
            return CRS.decode(identifier);
        } catch (FactoryException e1) {
            throw new RuntimeException("final crs cannot be resolved");
        }
    }

    /**
     * get crs from feature collection
     *
     * @param features input collection
     * @return crs of the input features
     */
    private @NotNull CoordinateReferenceSystem getCRS(@NotNull GTFeatureCollection features) {
        //return crs of first feature
        return (features.iterator().next().resolve()).getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
    }

    /**
     * reproject feature collection
     *
     * @param features   input features to be reprojected
     * @param featureCRS crs of the inputs
     * @param finalCRS   target crs
     * @return reprojected features
     */
    private @NotNull GTFeatureCollection reproject(@NotNull GTFeatureCollection features, @Nullable CoordinateReferenceSystem featureCRS, @Nullable CoordinateReferenceSystem finalCRS) {
        //check if transformation is required/applicable
        if (featureCRS == null || finalCRS == null || featureCRS.equals(finalCRS))
            return features;
        //init new collection
        List<SimpleFeature> features_proj = new ArrayList<>();
        //get transformation
        MathTransform transformation = getTransformation(featureCRS, finalCRS);
        //iterate collection and transform
        for (GTVectorFeature feature : features) {
            if(feature.getRepresentation() != null)
                features_proj.add(reproject(((GTVectorRepresentation) feature.getRepresentation()).resolve(), featureCRS, finalCRS, transformation));
        }
        //return
        return new GTFeatureCollection(features.getIdentifier(), DataUtilities.collection(features_proj), features.getMetadata());
    }

    /**
     * set or transform crs for simple feature
     *
     * @param feature        input feature
     * @param rangeCRS       target reference system
     * @param transformation crs transformation
     * @return transformed feature
     */
    private SimpleFeature reproject(@NotNull SimpleFeature feature, @Nullable CoordinateReferenceSystem domainCRS, @Nullable CoordinateReferenceSystem rangeCRS, @Nullable MathTransform transformation) {
        //return feature if sourceCRS = rangeCRS or one of the crs is null
        if (domainCRS == null || rangeCRS == null || domainCRS.equals(rangeCRS) || transformation == null)
            return feature;
        //build new featuretype
        SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder();
        //get input type
        SimpleFeatureType inputType = feature.getFeatureType();
        //configure ft builder
        ftBuilder.setName(inputType.getName());
        for (AttributeDescriptor desc : inputType.getAttributeDescriptors()) {
            //set crs for geometry types
            if (desc instanceof GeometryDescriptor) {
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
        SimpleFeatureBuilder fBuilder = new SimpleFeatureBuilder(newType);

        for (Property attribute : feature.getProperties()) {
            //transform geometry attribute if required
            if (attribute instanceof GeometryAttribute) {
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
     *
     * @param geom input geometry
     * @return transformed geometry
     */
    private Geometry reprojectGeometry(@NotNull Geometry geom, @NotNull MathTransform transformation) {
        try {
            return JTS.transform(geom, transformation);
        } catch (Exception e) {
            throw new RuntimeException("Could not reproject geometry", e);
        }
    }

    /**
     * get transformation
     *
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
     *
     * @param inCRS input CRS
     * @return output CRS with details
     * @throws FactoryException
     */
    private CoordinateReferenceSystem decodeCRSDetails(CoordinateReferenceSystem inCRS) throws FactoryException {
        String identifier = CRS.lookupIdentifier(inCRS, true);
        CoordinateReferenceSystem outCRS = CRS.decode(identifier);
        //check axis order, if they don't match, setRDFProperty longitude first (WGS84 issue)
        if (CRS.getAxisOrder(inCRS) != CRS.getAxisOrder(outCRS))
            outCRS = CRS.decode(identifier, true);
        //recheck axis order
        if (CRS.getAxisOrder(inCRS) != CRS.getAxisOrder(outCRS))
            throw new RuntimeException("CRS decoding resulted in incorrect axis order");
        return outCRS;
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_DOMAIN_TITLE, IN_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_RANGE_TITLE, IN_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class)},
                null,
                null);
        addInputConnector(IN_CRS_TITLE, IN_CRS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class)},
                null,
                null);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_DOMAIN_TITLE, OUT_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null);
        addOutputConnector(OUT_RANGE_TITLE, OUT_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class)},
                null);
    }

}
