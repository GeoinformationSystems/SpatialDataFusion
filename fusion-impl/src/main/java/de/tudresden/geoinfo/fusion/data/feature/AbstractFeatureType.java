package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

/**
 * feature type implementation
 */
public abstract class AbstractFeatureType extends Data implements IFeatureType {

    private IFeatureConcept concept;
    private Collection<IFeatureRepresentation> representations;

    /**
     * constructor
     *
     * @param identifier type identifier
     * @param type       type object
     */
    public AbstractFeatureType(@Nullable IIdentifier identifier, @NotNull Object type, @Nullable IMetadata metadata) {
        super(identifier, type, metadata);
    }

    @NotNull
    @Override
    public IFeatureConcept getRelatedConcept() {
        return concept;
    }

    /**
     * set feature concept
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
     * adds a feature representation
     *
     * @param representation associated representation
     */
    public void addRelatedRepresentation(@NotNull IFeatureRepresentation representation) {
        if (representations == null)
            representations = new HashSet<>();
        representations.add(representation);
    }

}
