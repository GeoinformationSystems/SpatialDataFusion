package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataElement;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.operation.IOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * measurement data implementation
 *
 * @param <T> measurement data type
 */
public class Measurement<T extends Comparable<T>> extends DataSubject<T> implements IMeasurement<T> {

    private final static IIdentifier MEASUREMENT_OPERATION = MetadataVocabulary.MEASUREMENT_OPERATION.getIdentifier();
    private final static IIdentifier MEASUREMENT_VALUE_RANGE = MetadataVocabulary.MEASUREMENT_VALUE_RANGE.getIdentifier();
    private final static IIdentifier MEASUREMENT_UOM = MetadataVocabulary.MEASUREMENT_UOM.getIdentifier();

    private IRDFLiteral literal;

    /**
     * constructor
     *
     * @param literal                measurement literal
     * @param metadata             measurement metadata
     */
    public Measurement(@NotNull LiteralData<T> literal, @Nullable IMetadata metadata) {
        super(literal.getIdentifier(), literal.resolve(), literal.getMetadata());
        this.literal = literal;
        this.initRDFSubject();
    }

    /**
     * Constructor for default measurement metadata
     * @param literal                measurement literal
     * @param dc_title         dublin core title
     * @param dc_abstract      dublin core description
     * @param uom              unit of measurement
     * @param measurementRange measurement range
     */
    public Measurement(@NotNull LiteralData<T> literal, @Nullable String dc_title, @Nullable String dc_abstract, @Nullable IIdentifier uom, @Nullable IMeasurementRange measurementRange, @Nullable IOperation operation) {
        this(literal, new Metadata(dc_title, dc_abstract));
        if(uom != null)
            this.addMetadataElement(new MetadataElement(MetadataVocabulary.MEASUREMENT_UOM.getIdentifier(), uom));
        if(measurementRange != null)
            this.addMetadataElement(new MetadataElement(MetadataVocabulary.MEASUREMENT_VALUE_RANGE.getIdentifier(), measurementRange));
        if(operation != null)
            this.addMetadataElement(new MetadataElement(MetadataVocabulary.MEASUREMENT_OPERATION.getIdentifier(), operation));
    }

    /**
     * initialize RDF subject
     */
    private void initRDFSubject() {
        this.addObject(Predicates.VALUE.getResource(), literal);
    }

    @Nullable
    @Override
    public IOperation getMeasurementOperation() {
        Object md_uom = this.getMetadataObject(MEASUREMENT_OPERATION);
        if (md_uom != null && md_uom instanceof IOperation)
            return (IOperation) md_uom;
        return null;
    }

    @Nullable
    @Override
    public IMeasurementRange<T> getMeasurementRange() {
        Object md_range = this.getMetadataObject(MEASUREMENT_VALUE_RANGE);
        if (md_range != null && md_range instanceof IMeasurementRange)
            //noinspection unchecked
            return (IMeasurementRange<T>) md_range;
        return null;
    }

    @Nullable
    @Override
    public IIdentifier getUnitOfMeasurement() {
        Object md_uom = this.getMetadataObject(MEASUREMENT_UOM);
        if (md_uom != null && md_uom instanceof IIdentifier)
            return (IIdentifier) md_uom;
        return null;
    }

    @Override
    public int compareTo(@NotNull IMeasurement<T> o) {
        return this.resolve().compareTo(o.resolve());
    }

}
