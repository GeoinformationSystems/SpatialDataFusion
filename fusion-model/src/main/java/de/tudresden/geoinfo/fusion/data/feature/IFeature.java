package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * feature implementation
 */
public interface IFeature extends IData, IRDFResource {

    /**
     * get concept of this feature
     *
     * @return feature concept
     */
    @Nullable
    IFeatureConcept getConcept();

    /**
     * get type of this feature
     *
     * @return feature type
     */
    @Nullable
    IFeatureType getType();

    /**
     * get entity of this feature
     *
     * @return feature entity
     */
    @Nullable
    IFeatureEntity getEntity();

    /**
     * get representation of this feature
     *
     * @return feature representation
     */
    @Nullable
    IFeatureRepresentation getRepresentation();

    /**
     * get all relations attached to this feature
     *
     * @return all feature relations
     */
    @NotNull
    Set<? extends IRelation> getRelations();

}
