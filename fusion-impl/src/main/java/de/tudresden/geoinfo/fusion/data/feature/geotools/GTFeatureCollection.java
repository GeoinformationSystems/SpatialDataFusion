package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeature;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * GeoTools feature collection implementation
 */
public class GTFeatureCollection extends AbstractFeatureCollection<GTVectorFeature> {

    private ReferencedEnvelope envelope;

    /**
     * constructor
     *
     * @param identifier        collection identifier
     * @param featureCollection GT feature collection
     */
    public GTFeatureCollection(@Nullable IIdentifier identifier, @NotNull Collection<GTVectorFeature> featureCollection, @Nullable IMetadata metadata) {
        super(identifier, featureCollection, metadata);
    }

    /**
     * constructor
     *
     * @param identifier        resource identifier
     * @param featureCollection GeoTools feature collection
     */
    public GTFeatureCollection(@Nullable IIdentifier identifier, @NotNull FeatureCollection featureCollection, @Nullable IMetadata metadata) {
        this(identifier, getGTCollection(identifier, featureCollection), metadata);
    }

    /**
     * create collection from GeoTools feature collection
     *
     * @param featureCollection input collection
     * @return collection of GTVectorFeature implementations
     */
    @NotNull
    public static Collection<GTVectorFeature> getGTCollection(@Nullable IIdentifier collectionId, @NotNull FeatureCollection featureCollection) {
        Collection<GTVectorFeature> collection = new HashSet<>();
        try (FeatureIterator iterator = featureCollection.features()) {
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                IIdentifier featureID = collectionId == null ? new Identifier(feature.getIdentifier().getID()) : new Identifier((collectionId + "#" + feature.getIdentifier().getID()));
                collection.add(new GTVectorFeature(featureID, feature, null));
            }
        }
        return collection;
    }

    /**
     * get GeoTools FeatureCollection
     *
     * @return GeoTools FeatureCollection
     */
    @NotNull
    public SimpleFeatureCollection collection() {
        List<SimpleFeature> featureList = new ArrayList<>();
        for (AbstractFeature feature : resolve()) {
            featureList.add((SimpleFeature) feature.resolve());
        }
        return DataUtilities.collection(featureList);
    }

    @Override
    public Envelope getBounds() {
        if (this.envelope == null) {
            for (GTVectorFeature feature : resolve()) {
                if (feature.getRepresentation() == null || feature.getRepresentation().getBounds() == null)
                    continue;
                if (this.envelope == null) {
                    this.envelope = new ReferencedEnvelope(feature.getRepresentation().getBounds());
                    continue;
                }
                expandEnvelope(feature.getRepresentationView().getBounds());
            }
        }
        return envelope;
    }

    @Override
    public void add(@NotNull GTVectorFeature feature) {
        super.add(feature);
        if (feature.getRepresentation() != null && envelope != null)
            expandEnvelope(feature.getRepresentation().getBounds());
    }

    /**
     * expand envelope with input envelope
     *
     * @param envelope input envelope
     */
    private void expandEnvelope(Envelope envelope) {
        if (envelope != null)
            this.envelope.expandToInclude(new ReferencedEnvelope(envelope));
    }

    @Override
    public CoordinateReferenceSystem getReferenceSystem() {
        //return first crs in collection
        for (GTVectorFeature feature : this.resolve()) {
            if (feature.getRepresentation() != null && feature.getRepresentation().getReferenceSystem() != null)
                return feature.getRepresentation().getReferenceSystem();
        }
        return null;
    }

}
