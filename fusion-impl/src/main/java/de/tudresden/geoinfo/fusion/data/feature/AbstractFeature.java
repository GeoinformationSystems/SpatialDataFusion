package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * abstract feature instance
 */
public abstract class AbstractFeature extends Data implements IFeature {

    private AbstractFeatureConcept concept;
    private AbstractFeatureType type;
    private AbstractFeatureEntity entity;
    private AbstractFeatureRepresentation representation;
    private Set<IRelation<? extends IFeature>> relations;

    /**
     * constructor
     *
     * @param identifier feature identifier
     * @param feature    feature object
     * @param relations  feature relations
     */
    public AbstractFeature(@Nullable IIdentifier identifier, @NotNull Object feature, @Nullable IMetadata metadata, @Nullable Set<IRelation<? extends IFeature>> relations) {
        super(identifier, feature, metadata);
        this.relations = relations;
    }

    @Override
    public @Nullable AbstractFeatureConcept getConcept() {
        if (concept == null)
            concept = getConceptView();
        return concept;
    }

    @Override
    public @NotNull AbstractFeatureType getType() {
        if (type == null)
            type = getTypeView();
        return type;
    }

    @Override
    public @NotNull AbstractFeatureEntity getEntity() {
        if (entity == null)
            entity = getEntityView();
        return entity;
    }

    @Override
    public @NotNull AbstractFeatureRepresentation getRepresentation() {
        if (representation == null)
            representation = getRepresentationView();
        return representation;
    }

    @NotNull
    @Override
    public Set<IRelation<? extends IFeature>> getRelations() {
        if (this.relations == null)
            this.relations = new HashSet<>();
        return this.relations;
    }

    /**
     * add a relation to this feature
     *
     * @param relation input feature relation
     */
    public void addRelation(@NotNull IRelation<? extends IFeature> relation) {
        this.getRelations().add(relation);
    }

    /**
     * initialize feature concept
     *
     * @return feature concept
     */
    public abstract AbstractFeatureConcept getConceptView();

    /**
     * initialize feature type
     *
     * @return feature type
     */
    public abstract AbstractFeatureType getTypeView();

    /**
     * initialize feature entity
     *
     * @return feature entity
     */
    public abstract AbstractFeatureEntity getEntityView();

    /**
     * initialize feature representation
     *
     * @return feature representation
     */
    public abstract AbstractFeatureRepresentation getRepresentationView();

}
