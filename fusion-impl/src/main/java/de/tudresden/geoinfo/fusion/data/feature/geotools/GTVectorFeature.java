package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.*;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Set;

/**
 * GeoTools feature implementation
 */
public class GTVectorFeature extends AbstractFeature {

    /**
     * constructor
     *
     * @param identifier feature identifier
     * @param feature    feature object
     * @param relations  feature relations
     */
    public GTVectorFeature(@Nullable IIdentifier identifier, @NotNull SimpleFeature feature, @Nullable IMetadata metadata, @Nullable Set<IRelation<? extends IFeature>> relations) {
        super(identifier, feature, metadata, relations);
    }

    /**
     * constructor
     *
     * @param identifier feature identifier
     * @param feature    feature object
     */
    public GTVectorFeature(@Nullable IIdentifier identifier, @NotNull SimpleFeature feature, @Nullable IMetadata metadata) {
        this(identifier, feature, metadata, null);
    }

    @NotNull
    @Override
    public SimpleFeature resolve() {
        return (SimpleFeature) super.resolve();
    }

    @Override
    public AbstractFeatureRepresentation getRepresentationView() {
        return new GTVectorRepresentation(new Identifier((resolve()).getID()), resolve(), null);
    }

    @Override
    public AbstractFeatureEntity getEntityView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractFeatureType getTypeView() {
        return new GTFeatureType(new Identifier((resolve()).getFeatureType().getTypeName()), (resolve()).getFeatureType(), null);
    }

    @Override
    public AbstractFeatureConcept getConceptView() {
        // TODO Auto-generated method stub
        return null;
    }

}
