package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.DataSubject;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * feature representation implementation
 */
public abstract class AbstractFeatureRepresentation<T> extends DataSubject<T> implements IFeatureRepresentation {

    private IFeatureType type;
    private IFeatureEntity entity;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param representation representation object
     */
    public AbstractFeatureRepresentation(@NotNull IIdentifier identifier, @NotNull T representation, @Nullable IMetadata metadata) {
        super(identifier, representation, metadata);
    }

    @NotNull
    @Override
    public IFeatureType getRelatedType() {
        return type;
    }

    /**
     * set feature type
     *
     * @param type associated type
     */
    public void setRelatedType(@NotNull IFeatureType type) {
        this.type = type;
    }

    @NotNull
    @Override
    public IFeatureEntity getRelatedEntity() {
        return entity;
    }

    /**
     * set feature entity
     *
     * @param entity associated entity
     */
    public void setRelatedEntity(@NotNull IFeatureEntity entity) {
        this.entity = entity;
    }

}
