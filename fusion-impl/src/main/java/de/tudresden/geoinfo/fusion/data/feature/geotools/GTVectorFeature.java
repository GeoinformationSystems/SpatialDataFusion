package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeature;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureConcept;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureEntity;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Set;

/**
 * GeoTools feature implementation
 */
public class GTVectorFeature extends AbstractFeature<SimpleFeature> {

    /**
     * constructor
     *
     * @param identifier identifier
     * @param feature    feature object
     * @param relations  feature relations
     */
    public GTVectorFeature(@NotNull IIdentifier identifier, @NotNull SimpleFeature feature, @Nullable IMetadata metadata, @Nullable Set<IRelation> relations) {
        super(identifier, feature, metadata, relations);
    }

    @Override
    public GTVectorRepresentation initRepresentation() {
        return new GTVectorRepresentation(new ResourceIdentifier(null, resolve().getID()), resolve(), null);
    }

    @Override
    public AbstractFeatureEntity initEntity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GTFeatureType initType() {
        return new GTFeatureType(new ResourceIdentifier(null, resolve().getFeatureType().getTypeName()), resolve().getFeatureType(), null);
    }

    @Override
    public AbstractFeatureConcept initConcept() {
        // TODO Auto-generated method stub
        return null;
    }

}
