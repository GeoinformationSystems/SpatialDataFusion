package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * collection of feature relations
 */
public class BinaryFeatureRelationCollection extends DataCollection<BinaryFeatureRelation> {

    /**
     * feature identifier with associated relations
     */
    private HashMap<IIdentifier, Set<BinaryFeatureRelation>> relationIndex;

    /**
     * constructor
     *
     * @param identifier resource identifier
     * @param relations  input relations
     */
    public BinaryFeatureRelationCollection(@Nullable IIdentifier identifier, @Nullable Collection<BinaryFeatureRelation> relations, @Nullable IMetadata metadata) {
        super(identifier, relations != null ? relations : new HashSet<>(), metadata);
        relationIndex = new HashMap<>();
        for (BinaryFeatureRelation relation : this.resolve()) {
            this.addToIndex(relation);
        }
    }

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    public BinaryFeatureRelationCollection(@Nullable IIdentifier identifier, @Nullable IMetadata metadata) {
        this(identifier, new HashSet<>(), metadata);
    }

    /**
     * add relation to index
     *
     * @param measurement input measurement
     */
    private void addToIndex(@NotNull BinaryFeatureRelation measurement) {
        addToIndex(measurement.getDomain().getIdentifier(), measurement);
        addToIndex(measurement.getRange().getIdentifier(), measurement);
    }

    /**
     * add relation to key
     *
     * @param key      relation key
     * @param relation relation to associate with key
     */
    public void addToIndex(@NotNull IIdentifier key, @NotNull BinaryFeatureRelation relation) {
        if (relationIndex.containsKey(key))
            relationIndex.get(key).add(relation);
        else {
            Set<BinaryFeatureRelation> relations = Sets.newHashSet(relation);
            relationIndex.put(key, relations);
        }
    }

    /**
     * add relation to relations
     *
     * @param relation input relation
     */
    public void add(@NotNull BinaryFeatureRelation relation) {
        this.resolve().add(relation);
        this.addToIndex(relation);
    }

    /**
     * add relation collection to index
     *
     * @param relations input relations
     */
    public void addAll(@NotNull Collection<BinaryFeatureRelation> relations) {
        for (BinaryFeatureRelation relation : relations) {
            add(relation);
        }
    }

    /**
     * get measurements associated with an identifier
     *
     * @param identifier input identifier
     * @return measurements associated with input feature
     */
    public Set<BinaryFeatureRelation> getMeasurements(IIdentifier identifier) {
        return relationIndex.get(identifier);
    }

    /**
     * get all features with a particular role
     *
     * @return all features for the input role
     */
    @NotNull
    private Collection<IFeature> getFeatures(IRole role) {
        Set<IFeature> collection = new HashSet<>();
        for (IRelation<? extends IFeature> relation : this.resolve()) {
            collection.addAll(relation.getMembers(role));
        }
        return collection;
    }

    /**
     * get relations associated with identifier
     *
     * @param identifier input identifier
     * @return relations associated with input identifier
     */
    @NotNull
    public Set<BinaryFeatureRelation> getRelations(IIdentifier identifier) {
        return relationIndex.get(identifier);
    }

}
