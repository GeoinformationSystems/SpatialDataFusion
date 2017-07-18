package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.DataSubject;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

/**
 * feature entity implementation
 */
public abstract class AbstractFeatureEntity<T> extends DataSubject<T> implements IFeatureEntity {

    private IFeatureConcept concept;
    private Collection<IFeatureRepresentation> representations;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param entity     entity object
     */
    public AbstractFeatureEntity(@NotNull IIdentifier identifier, @NotNull T entity, @Nullable IMetadata metadata) {
        super(identifier, entity, metadata);
    }

    @NotNull
    @Override
    public IFeatureConcept getRelatedConcept() {
        return concept;
    }

    /**
     * set associated feature concept
     *
     * @param concept associated concept
     */
    public void setRelatedConcept(@NotNull IFeatureConcept concept) {
        this.concept = concept;
    }

    @NotNull
    @Override
    public Collection<IFeatureRepresentation> getRelatedRepresentations() {
        return representations;
    }

    /**
     * adds an associated feature representation
     *
     * @param representation associated representation
     */
    public void addRelatedRepresentation(@NotNull IFeatureRepresentation representation) {
        if (representations == null)
            representations = new HashSet<>();
        representations.add(representation);
    }

}
