package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.Measurement;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * relation measurement implementation
 */
public class RelationMeasurement<T extends Comparable<T>> extends Measurement<T> implements IRelationMeasurement<T> {

    private static final IResource TYPE = Objects.RELATION_MEASUREMENT.getResource();
    private IResource domain, range;

    /**
     * constructor
     *
     * @param domain measurement domain
     * @param range  measurement range
     */
    public RelationMeasurement(@Nullable IIdentifier identifier, @NotNull IResource domain, @NotNull IResource range, @NotNull T value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<T> measurementRange, @NotNull IResource uom) {
        super(identifier, value, metadata, TYPE, measurementOperation, measurementRange, uom);
        this.domain = domain;
        this.range = range;
    }

    /**
     * constructor
     *
     * @param domain measurement domain
     * @param range  measurement range
     */
    public RelationMeasurement(@Nullable IIdentifier identifier, @NotNull IResource domain, @NotNull IResource range, @NotNull T value, @NotNull String title, @Nullable String description, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<T> measurementRange, @NotNull IResource uom) {
        super(identifier, value, title, description, TYPE, measurementOperation, measurementRange, uom);
        this.domain = domain;
        this.range = range;
    }

    @NotNull
    @Override
    public IResource getDomain() {
        return this.domain;
    }

    @NotNull
    @Override
    public IResource getRange() {
        return this.range;
    }

}
