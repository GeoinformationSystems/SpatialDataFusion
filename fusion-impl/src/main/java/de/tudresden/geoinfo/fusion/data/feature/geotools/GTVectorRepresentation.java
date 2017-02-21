package de.tudresden.geoinfo.fusion.data.feature.geotools;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.WKTLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * GeoTools implementation of a feature representation
 */
public class GTVectorRepresentation extends AbstractFeatureRepresentation {

    /**
     * constructor
     *
     * @param identifier     representation identifier
     * @param representation simple feature
     */
    public GTVectorRepresentation(@Nullable IIdentifier identifier, @NotNull SimpleFeature representation, @Nullable IMetadata metadata) {
        super(identifier, representation, metadata);
    }

    @NotNull
    @Override
    public SimpleFeature resolve() {
        return (SimpleFeature) super.resolve();
    }

    @Override
    public Object getProperty(@NotNull IIdentifier identifier) {
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
     *
     * @return WKT geometry of the feature
     */
    @Nullable
    public WKTLiteral getWKTGeometry() {
        return this.getDefaultGeometry() != null ? new WKTLiteral(getDefaultGeometry().toText()) : null;
    }

}
