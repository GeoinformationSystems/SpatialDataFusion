package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.DataSubject;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * abstract feature instance
 */
public abstract class AbstractFeature<T> extends DataSubject<T> implements IFeature {

    private AbstractFeatureConcept concept;
    private AbstractFeatureType type;
    private AbstractFeatureEntity entity;
    private AbstractFeatureRepresentation representation;
    private Set<IRelation> relations;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param feature    feature object
     * @param relations  feature relations
     */
    public AbstractFeature(@NotNull IIdentifier identifier, @NotNull T feature, @Nullable IMetadata metadata, @Nullable Set<IRelation> relations) {
        super(identifier, feature, metadata);
        this.relations = relations;
    }

    @Override
    public @Nullable AbstractFeatureConcept getConcept() {
        if (concept == null)
            concept = initConcept();
        return concept;
    }

    @Override
    public @Nullable AbstractFeatureType getType() {
        if (type == null)
            type = initType();
        return type;
    }

    @Override
    public @Nullable AbstractFeatureEntity getEntity() {
        if (entity == null)
            entity = initEntity();
        return entity;
    }

    @Override
    public @Nullable AbstractFeatureRepresentation getRepresentation() {
        if (representation == null)
            representation = initRepresentation();
        return representation;
    }

    @NotNull
    @Override
    public Set<IRelation> getRelations() {
        if (this.relations == null)
            this.relations = new HashSet<>();
        return this.relations;
    }

    /**
     * add a relation to this feature
     *
     * @param relation input feature relation
     */
    public void addRelation(@NotNull IRelation relation) {
        this.getRelations().add(relation);
    }

    /**
     * initialize feature concept
     *
     * @return feature concept
     */
    public abstract AbstractFeatureConcept initConcept();

    /**
     * initialize feature type
     *
     * @return feature type
     */
    public abstract AbstractFeatureType initType();

    /**
     * initialize feature entity
     *
     * @return feature entity
     */
    public abstract AbstractFeatureEntity initEntity();

    /**
     * initialize feature representation
     *
     * @return feature representation
     */
    public abstract AbstractFeatureRepresentation initRepresentation();

}
