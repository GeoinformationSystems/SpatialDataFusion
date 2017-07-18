package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.*;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * GeoTools feature implementation
 */
public class GTGridFeature extends AbstractFeature {

    /**
     * constructor
     *
     * @param identifier local identifier
     * @param feature    GT grid object
     * @param relations  feature relations
     */
    public GTGridFeature(@NotNull IIdentifier identifier, @NotNull GridCoverage2D feature, @Nullable IMetadata metadata, @Nullable Set<IRelation> relations) {
        super(identifier, feature, metadata, relations);
    }

    /**
     * constructor
     *
     * @param identifier identifier
     * @param feature    GT grid object
     */
    public GTGridFeature(@NotNull IIdentifier identifier, @NotNull GridCoverage2D feature, @Nullable IMetadata metadata) {
        this(identifier, feature, metadata, null);
    }

    /**
     * constructor
     *
     * @param identifier identifier
     * @param file       coverage file
     * @param relations  feature relations
     */
    public GTGridFeature(@NotNull IIdentifier identifier, @NotNull File file, @Nullable IMetadata metadata, @Nullable Set<IRelation> relations) throws IOException {
        this(identifier, getCoverage(file), metadata, relations);
    }

    /**
     * constructor
     *
     * @param identifier identifier
     * @param file       GT grid file
     */
    public GTGridFeature(@NotNull IIdentifier identifier, @NotNull File file, @Nullable IMetadata metadata) throws IOException {
        this(identifier, file, metadata, null);
    }

    /**
     * get coverage from file
     *
     * @param file input file
     * @return GeoTools coverage object
     * @throws IOException
     */
    @NotNull
    public static GridCoverage2D getCoverage(@NotNull File file) throws IOException {
        if(!file.exists() || file.isDirectory())
            throw new IOException("Coverage file does not exist");
        AbstractGridFormat format = GridFormatFinder.findFormat(file);
        if (format == null)
            throw new IOException("No applicable coverage reader found");
        GridCoverage2DReader reader = format.getReader(file);
        return reader.read(null);
    }

    @NotNull
    @Override
    public GridCoverage2D resolve() {
        return (GridCoverage2D) super.resolve();
    }

    @Override
    public AbstractFeatureRepresentation initRepresentation() {
        return new GTGridRepresentation(new ResourceIdentifier(), resolve(), null);
    }

    @Override
    public AbstractFeatureEntity initEntity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractFeatureType initType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractFeatureConcept initConcept() {
        // TODO Auto-generated method stub
        return null;
    }

}
