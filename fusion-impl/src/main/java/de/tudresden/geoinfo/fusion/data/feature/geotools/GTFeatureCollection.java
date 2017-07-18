package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataElement;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataVocabulary;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.*;

/**
 * GeoTools feature collection implementation
 */
public class GTFeatureCollection extends AbstractFeatureCollection<GTVectorFeature> {

    private ReferencedEnvelope envelope;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param features GT feature map
     */
    public GTFeatureCollection(@NotNull IIdentifier identifier, @NotNull Collection<GTVectorFeature> features, @Nullable IMetadata metadata) {
        super(identifier, features, metadata);
    }

    /**
     * constructor
     *
     * @param identifier identifier
     * @param featureCollection GeoTools feature collection
     */
    public GTFeatureCollection(@NotNull IIdentifier identifier, @NotNull SimpleFeatureCollection featureCollection, @Nullable IMetadata metadata) {
        this(identifier, getGTCollection(identifier, featureCollection), metadata);
    }

    /**
     * create collection from GeoTools feature collection
     *
     * @param featureCollection input collection
     * @return collection of GTVectorFeature implementations
     */
    @NotNull
    public static Collection<GTVectorFeature> getGTCollection(@NotNull IIdentifier identifier, @NotNull SimpleFeatureCollection featureCollection) {
        Set<GTVectorFeature> collection = new HashSet<>();
        try (SimpleFeatureIterator iterator = featureCollection.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                String fid = feature.getIdentifier().getID();
                Metadata metadata = new Metadata();
                metadata.addElement(new MetadataElement(MetadataVocabulary.DC_TITLE.getIdentifier(), fid));
                metadata.addElement(new MetadataElement(MetadataVocabulary.DC_SOURCE.getIdentifier(), identifier.getGlobalIdentifier()));
                IIdentifier featureID = new ResourceIdentifier(identifier.getGlobalIdentifier() + "#" + fid, fid);
                collection.add(new GTVectorFeature(featureID, feature, metadata, null));
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
        for (GTVectorFeature feature : this) {
            featureList.add(feature.resolve());
        }
        return DataUtilities.collection(featureList);
    }

    @Override
    public Envelope getBounds() {
        if (this.envelope == null) {
            for (GTVectorFeature feature : this) {
                if (feature.getRepresentation() == null || feature.getRepresentation().getBounds() == null)
                    continue;
                if (this.envelope == null) {
                    this.envelope = new ReferencedEnvelope(feature.getRepresentation().getBounds());
                    continue;
                }
                expandEnvelope(feature.initRepresentation().getBounds());
            }
        }
        return envelope;
    }

    @Override
    public boolean add(@NotNull GTVectorFeature feature) {
        if (feature.getRepresentation() != null && envelope != null)
            expandEnvelope(feature.getRepresentation().getBounds());
        return super.add(feature);
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
        for (GTVectorFeature feature : this) {
            if (feature.getRepresentation() != null && feature.getRepresentation().getReferenceSystem() != null)
                return feature.getRepresentation().getReferenceSystem();
        }
        return null;
    }

}
