package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * collection of relation measurements
 */
public class RelationMeasurementCollection extends DataCollection<IRelationMeasurement> {

    /**
     * feature identifier with associated relations
     */
    private HashMap<IIdentifier, Set<IRelationMeasurement>> measurementIndex;

    /**
     * constructor
     *
     * @param identifier   resource identifier
     * @param measurements input measurements
     */
    public RelationMeasurementCollection(@Nullable IIdentifier identifier, @Nullable Collection<IRelationMeasurement> measurements, @Nullable IMetadata metadata) {
        super(identifier, measurements != null ? measurements : new HashSet<>(), metadata);
        measurementIndex = new HashMap<>();
        for (IRelationMeasurement measurement : this.resolve()) {
            this.addToIndex(measurement);
        }
    }

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    public RelationMeasurementCollection(@Nullable IIdentifier identifier, @Nullable IMetadata metadata) {
        this(identifier, new HashSet<>(), metadata);
    }

    /**
     * add relation to index
     *
     * @param measurement input measurement
     */
    public void addToIndex(IRelationMeasurement measurement) {
        addToIndex(measurement.getDomain().getIdentifier(), measurement);
        addToIndex(measurement.getRange().getIdentifier(), measurement);
    }

    /**
     * add relation to key
     *
     * @param key         relation key
     * @param measurement relation to associate with key
     */
    public void addToIndex(@NotNull IIdentifier key, @NotNull IRelationMeasurement measurement) {
        if (measurementIndex.containsKey(key))
            measurementIndex.get(key).add(measurement);
        else {
            Set<IRelationMeasurement> measurements = Sets.newHashSet(measurement);
            measurementIndex.put(key, measurements);
        }
    }

    /**
     * add measurement to measurements
     *
     * @param measurement input measurement
     */
    public void add(@NotNull IRelationMeasurement measurement) {
        if (measurement instanceof RelationMeasurement)
            this.resolve().add(measurement);
    }

    /**
     * add relation collection to index
     *
     * @param measurements input relations
     */
    public void addAll(@NotNull Collection<IRelationMeasurement> measurements) {
        for (IRelationMeasurement measurement : measurements) {
            add(measurement);
        }
    }

    /**
     * get measurements associated with an identifier
     *
     * @param identifier input identifier
     * @return measurements associated with input identifier
     */
    public Set<IRelationMeasurement> getMeasurements(IIdentifier identifier) {
        return measurementIndex.get(identifier);
    }

}
