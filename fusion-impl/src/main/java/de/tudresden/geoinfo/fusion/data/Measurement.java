package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * measurement data implementation
 *
 * @param <T> measurement data type
 */
public class Measurement<T extends Comparable<T>> extends LiteralData<T> implements IMeasurement<T> {

    private IResource measurementOperation;
    private IMeasurementRange<T> measurementRange;
    private IResource uom;

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                measurement value
     * @param metadata             measurement metadata
     * @param measurementOperation measurement operation
     * @param measurementRange     associated range of measurements
     * @param uom                  unit of measurement
     */
    public Measurement(@Nullable IIdentifier identifier, @NotNull T value, @Nullable IMetadata metadata, @NotNull IResource dataType, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<T> measurementRange, @NotNull IResource uom) {
        super(identifier, value, metadata, dataType);
        this.measurementOperation = measurementOperation;
        this.measurementRange = measurementRange;
        this.uom = uom;
    }

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                measurement value
     * @param title                literal title
     * @param description          literal description
     * @param measurementOperation measurement operation
     * @param measurementRange     associated range of measurements
     * @param uom                  unit of measurement
     */
    public Measurement(@Nullable IIdentifier identifier, @NotNull T value, @NotNull String title, @Nullable String description, @NotNull IResource dataType, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<T> measurementRange, @NotNull IResource uom) {
        super(identifier, value, new Metadata(title, description), dataType);
        this.measurementOperation = measurementOperation;
        this.measurementRange = measurementRange;
        this.uom = uom;
    }

    @NotNull
    @Override
    public IResource getMeasurementOperation() {
        return measurementOperation;
    }

    @NotNull
    @Override
    public IMeasurementRange<T> getMeasurementRange() {
        return measurementRange;
    }

    @NotNull
    @Override
    public IResource getUnitOfMeasurement() {
        return uom;
    }

    @Override
    public int compareTo(@NotNull IMeasurement<T> o) {
        return this.resolve().compareTo(o.resolve());
    }
}
