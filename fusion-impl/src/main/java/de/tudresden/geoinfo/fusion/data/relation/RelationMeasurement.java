package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.Measurement;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
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
    public RelationMeasurement(@Nullable IIdentifier identifier, @NotNull IResource domain, @NotNull IResource range, @NotNull T value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, TYPE, measurementOperation);
        this.domain = domain;
        this.range = range;
    }

    protected void initRDFSubject() {
        super.initRDFSubject();
        this.setRDFProperty(Predicates.HAS_DOMAIN.getResource(), this.getDomain());
        this.setRDFProperty(Predicates.HAS_RANGE.getResource(), this.getRange());
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
