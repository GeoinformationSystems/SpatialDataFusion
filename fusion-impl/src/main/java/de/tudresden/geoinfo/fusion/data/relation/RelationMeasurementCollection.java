package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
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
     * feature identifier with associated measurements
     */
    private HashMap<IRDFResource, Set<IRelationMeasurement>> measurementIndex;

    /**
     * constructor
     * @param identifier local identifier
     * @param measurements input measurements
     * @param metadata metadata
     */
    public RelationMeasurementCollection(@NotNull IIdentifier identifier, @Nullable Collection<IRelationMeasurement> measurements, @Nullable IMetadata metadata) {
        super(identifier, measurements != null ? measurements : new HashSet<>(), metadata);
        measurementIndex = new HashMap<>();
        for (IRelationMeasurement measurement : this.resolve()) {
            this.addToIndex(measurement);
        }
    }

    /**
     * constructor
     *
     * @param identifier local identifier
     * @param metadata metadata
     */
    public RelationMeasurementCollection(@NotNull IIdentifier identifier, @Nullable IMetadata metadata) {
        this(identifier, new HashSet<>(), metadata);
    }

    /**
     * add relation to index
     *
     * @param measurement input measurement
     */
    public void addToIndex(IRelationMeasurement measurement) {
        this.addToIndex(measurement.getDomain(), measurement);
        this.addToIndex(measurement.getRange(), measurement);
    }

    /**
     * add relation to key
     *
     * @param key         relation key
     * @param measurement relation to associate with key
     */
    public void addToIndex(@NotNull IRDFResource key, @NotNull IRelationMeasurement measurement) {
        if (measurementIndex.containsKey(key))
            measurementIndex.get(key).add(measurement);
        else {
            Set<IRelationMeasurement> measurements = Sets.newHashSet(measurement);
            measurementIndex.put(key, measurements);
        }
    }

    /**
     * get measurements associated with a resource
     *
     * @param resource input resource
     * @return measurements associated with input resource
     */
    public Set<IRelationMeasurement> getMeasurements(IRDFResource resource) {
        return measurementIndex.get(resource);
    }

}
