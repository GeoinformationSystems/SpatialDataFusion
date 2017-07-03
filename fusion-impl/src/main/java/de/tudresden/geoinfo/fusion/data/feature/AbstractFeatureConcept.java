package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Feature concept implementation
 */
public abstract class AbstractFeatureConcept extends Data implements IFeatureConcept {

    private Collection<IFeatureEntity> entities;
    private Collection<IFeatureType> types;

    /**
     * constructor
     *
     * @param identifier concept identifier
     * @param concept    concept object
     */
    public AbstractFeatureConcept(@Nullable IIdentifier identifier, @NotNull Object concept, @Nullable IMetadata metadata) {
        super(identifier, concept, metadata);
    }

    @NotNull
    @Override
    public Collection<IFeatureEntity> getRelatedEntities() {
        return entities;
    }

    @NotNull
    @Override
    public Collection<IFeatureType> getRelatedTypes() {
        return types;
    }

    /**
     * adds an associated feature type
     *
     * @param type associated type
     */
    public void addRelatedType(IFeatureType type) {
        if (types == null)
            types = new HashSet<>();
        types.add(type);
    }

    /**
     * adds an associated feature entity
     *
     * @param entity associated entity
     */
    public void addRelatedEntity(IFeatureEntity entity) {
        if (entities == null)
            entities = new HashSet<>();
        entities.add(entity);
    }

}
