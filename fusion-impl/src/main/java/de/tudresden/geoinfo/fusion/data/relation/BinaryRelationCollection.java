package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * collection of feature relations
 */
public class BinaryRelationCollection extends DataCollection<BinaryRelation> {

    private Map<IRDFResource,Set<BinaryRelation>> resourceIndex;

    /**
     * constructor
     *
     * @param identifier resource identifier
     * @param relations  input relations
     */
    public BinaryRelationCollection(@NotNull IIdentifier identifier, @Nullable Collection<BinaryRelation> relations, @Nullable IMetadata metadata) {
        super(identifier, relations != null ? relations : new HashSet<>(), metadata);
    }

    /**
     * add relation to index
     *
     * @param measurement input measurement
     */
    private void addToIndex(@NotNull BinaryRelation measurement) {
        if(this.resourceIndex == null)
            this.resourceIndex = new HashMap<>();
        addToIndex(measurement.getDomain(), measurement);
        addToIndex(measurement.getRange(), measurement);
    }

    /**
     * add relation to key
     *
     * @param key      relation key
     * @param relation relation to associate with key
     */
    private void addToIndex(@NotNull IRDFResource key, @NotNull BinaryRelation relation) {
        if (this.resourceIndex.containsKey(key))
            this.resourceIndex.get(key).add(relation);
        else {
            Set<BinaryRelation> relations = Sets.newHashSet(relation);
            this.resourceIndex.put(key, relations);
        }
    }

    /**
     * add relation to relations
     *
     * @param relation input relation
     */
    public boolean add(@NotNull BinaryRelation relation) {
        this.addToIndex(relation);
        return super.add(relation);
    }

    /**
     * get measurements associated with an identifier
     *
     * @param resource input resource
     * @return measurements associated with input feature
     */
    public Set<BinaryRelation> getMeasurements(@NotNull IRDFResource resource) {
        return this.resourceIndex.get(resource);
    }

    /**
     * get all resources associated with a particular role
     * @param role input role
     *
     * @return all features for the input role
     */
    @NotNull
    private Set<IRDFResource> getResourcesByRole(IRole role) {
        Set<IRDFResource> collection = new HashSet<>();
        for (IRelation relation : this) {
            collection.addAll(relation.getMembers(role));
        }
        return collection;
    }

}
