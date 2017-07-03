package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.*;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * measurement data implementation
 *
 * @param <T> measurement data type
 */
public class Measurement<T extends Comparable<T>> extends LiteralData<T> implements IMeasurement<T>, ISubject {

    private IResource measurementOperation;
    private Subject rdfSubject;

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                measurement value
     * @param metadata             measurement metadata
     * @param measurementOperation measurement operation
     */
    public Measurement(@Nullable IIdentifier identifier, @NotNull T value, @Nullable IMetadata metadata, @NotNull IResource dataType, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, dataType);
        this.measurementOperation = measurementOperation;
    }

    protected void initRDFSubject() {
        this.rdfSubject = new Subject(null);
        this.rdfSubject.put(Predicates.VALUE.getResource(), this.getLiteralObject());
        if(measurementOperation != null)
            this.rdfSubject.put(Predicates.OPERATION.getResource(), measurementOperation);
    }

    /**
     * create literal value as part of resource; prevents loop in RDF generation
     * @return literal object (copy of this object not implementing ISubject)
     */
    private @NotNull ILiteral getLiteralObject() {
        return new LiteralData<>(null, this.resolve(), null, this.getLiteralType());
    }

    @NotNull
    @Override
    public IResource getMeasurementOperation() {
        return measurementOperation;
    }

    @Nullable
    @Override
    public IMeasurementRange<T> getMeasurementRange() {
        IMetadataElement md_element = this.getMetadata().getElement(Predicates.MEASUREMENT_VALUE_RANGE.getResource());
        if (md_element != null && md_element instanceof IMeasurementRange)
            //noinspection unchecked
            return (IMeasurementRange<T>) md_element;
        return null;
    }

    @Nullable
    @Override
    public IResource getUnitOfMeasurement() {
        IMetadataElement md_element = this.getMetadata().getElement(Predicates.MEASUREMENT_UOM.getResource());
        if (md_element != null && md_element instanceof IResource)
            return (IResource) md_element;
        return null;
    }

    @Override
    public int compareTo(@NotNull IMeasurement<T> o) {
        return this.resolve().compareTo(o.resolve());
    }

    @Override
    public @NotNull Set<IResource> getPredicates() {
        return this.getRDFSubject().getPredicates();
    }

    @Override
    public @Nullable Set<INode> getObjects(@NotNull IResource predicate) {
        return this.getRDFSubject().getObjects(predicate);
    }

    protected void setRDFProperty(@NotNull IResource predicate, @NotNull INode object) {
        this.getRDFSubject().put(predicate, object);
    }

    protected void setRDFProperty(@NotNull IResource predicate, @NotNull Collection<INode> objectSet) {
        this.getRDFSubject().put(predicate, objectSet);
    }

    private Subject getRDFSubject() {
        if(this.rdfSubject == null)
            this.initRDFSubject();
        return this.rdfSubject;
    }

}
