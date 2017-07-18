package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.*;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.operation.IOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * relation measurement implementation
 */
public class RelationMeasurement<T extends Comparable<T>> extends Measurement<T> implements IRelationMeasurement<T> {

    private static final IRDFResource OBJECT_TYPE = Objects.RELATION_MEASUREMENT.getResource();
    private IRDFResource domain, range;

    /**
     * constructor
     * @param literal measurement literal
     * @param domain measurement domain
     * @param range measurement range
     * @param metadata measurement metadata
     */
    public RelationMeasurement(@NotNull LiteralData<T> literal, @NotNull IRDFResource domain, @NotNull IRDFResource range, @Nullable IMetadata metadata) {
        super(literal, metadata);
        this.domain = domain;
        this.range = range;
        this.initRDFSubject();
    }

    /**
     * constructor
     * @param literal measurement literal
     * @param domain measurement domain
     * @param range measurement range
     * @param dc_title measurement title
     * @param dc_abstract measurement description
     * @param uom unit of measurement
     * @param measurementRange measurement value range
     * @param operation measurement operation
     */
    public RelationMeasurement(@NotNull LiteralData<T> literal, @NotNull IRDFResource domain, @NotNull IRDFResource range, @Nullable String dc_title, @Nullable String dc_abstract, @Nullable IIdentifier uom, @Nullable IMeasurementRange measurementRange, @Nullable IOperation operation) {
        super(literal, dc_title, dc_abstract, uom, measurementRange, operation);
        this.domain = domain;
        this.range = range;
        this.initRDFSubject();
    }

    /**
     * initialize RDF subject
     */
    private void initRDFSubject() {
        this.addObject(Predicates.TYPE.getResource(), OBJECT_TYPE);
        this.addObject(Predicates.HAS_DOMAIN.getResource(), this.getDomain());
        this.addObject(Predicates.HAS_RANGE.getResource(), this.getRange());
    }

    @NotNull
    @Override
    public IRDFResource getDomain() {
        return this.domain;
    }

    @NotNull
    @Override
    public IRDFResource getRange() {
        return this.range;
    }

}
