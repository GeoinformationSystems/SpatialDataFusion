package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureRepresentation;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.geotools.coverage.grid.GridCoverage2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * GeoTools raster implementation
 */
public class GTGridRepresentation extends AbstractFeatureRepresentation {

    /**
     * constructor
     *
     * @param identifier representation identifier
     * @param coverage   GT coverage
     */
    public GTGridRepresentation(@Nullable IIdentifier identifier, @NotNull GridCoverage2D coverage, @Nullable IMetadata metadata) {
        super(identifier, coverage, metadata);
    }

    @NotNull
    @Override
    public GridCoverage2D resolve() {
        return (GridCoverage2D) super.resolve();
    }


    @Override
    public Object getProperty(@NotNull IIdentifier identifier) {
        return null;
    }

    @Nullable
    @Override
    public Object getDefaultGeometry() {
        return null;
    }

    @Nullable
    @Override
    public Envelope getBounds() {
        return this.resolve().getEnvelope();
    }

    @Override
    public CoordinateReferenceSystem getReferenceSystem() {
        return this.resolve().getCoordinateReferenceSystem();
    }
}
