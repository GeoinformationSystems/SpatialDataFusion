package de.tudresden.geoinfo.fusion.data.feature.geotools;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.WKTLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * GeoTools implementation of a feature representation
 */
public class GTVectorRepresentation extends AbstractFeatureRepresentation {

    private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
    private static IResource TYPE_REPRESENTATION = Objects.FEATURE_REPRESENTATION.getResource();
    private static IResource TYPE_COVERAGE = Objects.FEATURE.getResource();
    private static IResource PREDICATE_WKT = Predicates.asWKT.getResource();

    /**
     * constructor
     * @param identifier    feature identifier
     * @param representation feature representation
     * @param description    feature description
     */
    public GTVectorRepresentation(IIdentifier identifier, SimpleFeature representation, IMetadataForData description) {
        super(identifier, representation, description);
        initSubject();
    }

    /**
     * initialize coverage subject
     */
    private void initSubject() {
        //set resource type
        put(PREDICATE_TYPE, TYPE_REPRESENTATION);
        put(PREDICATE_TYPE, TYPE_COVERAGE);
    }

    @Override
    public SimpleFeature resolve() {
        return (SimpleFeature) super.resolve();
    }

    @Override
    public Object getProperty(IIdentifier identifier) {
        return null;
    }

    @Override
    public Geometry getDefaultGeometry() {
        return (Geometry) this.resolve().getDefaultGeometryProperty().getValue();
    }

    @Override
    public BoundingBox getBounds() {
        return this.resolve().getBounds();
    }

    @Override
    public CoordinateReferenceSystem getReferenceSystem() {
        return this.resolve().getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
    }

    /**
     * get WKT description of the feature geometry
     * @return WKT geometry of the feature
     */
    public WKTLiteral getWKTGeometry() {
        return new WKTLiteral(getDefaultGeometry().toText());
    }

    /**
     * add geometry as WKT to RDF representation (caution: large geometries are likely to cause memory issues)
     */
    public void showWKTGeometry(){
        put(PREDICATE_WKT, getWKTGeometry());
    }

    /**
     * hide geometry as WKT in RDF representation
     */
    public void hideWKTGeometry(){
        remove(PREDICATE_WKT);
    }

}
