package de.tudresden.geoinfo.fusion.data.feature.geotools;

import com.vividsolutions.jts.index.strtree.STRtree;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;

import java.util.Collection;
import java.util.List;

/**
 * GeoTools indexed feature collection implementation
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class GTIndexedFeatureCollection extends GTFeatureCollection {

    /**
     * bounding box index
     */
    private STRtree index;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param features collection object
     */
    public GTIndexedFeatureCollection(@NotNull IIdentifier identifier, @NotNull Collection<GTVectorFeature> features, @Nullable IMetadata metadata) {
        super(identifier, features, metadata);
        buildIndex();
    }

    /**
     * constructor
     *
     * @param identifier identifier
     * @param featureCollection GeoTools feature collection
     */
    public GTIndexedFeatureCollection(@NotNull IIdentifier identifier, @NotNull SimpleFeatureCollection featureCollection, @Nullable IMetadata metadata) {
        this(identifier, getGTCollection(identifier, featureCollection), metadata);
    }

    /**
     * constructor
     *
     * @param collection GeoTools GTVectorFeature collection
     */
    public GTIndexedFeatureCollection(@NotNull GTFeatureCollection collection) {
        this(collection.getIdentifier(), collection.resolve(), collection.getMetadata());
    }

    /**
     * build spatial index for feature collection
     */
    private void buildIndex() {
        this.index = new STRtree();
        for (GTVectorFeature feature : this) {
            addFeatureToIndex(feature);
        }
    }

    /**
     * adds feature to spatial index
     *
     * @param feature input feature
     */
    private void addFeatureToIndex(@NotNull GTVectorFeature feature) {
        if (feature.getRepresentation() != null && feature.getRepresentation().getBounds() != null)
            index.insert(new ReferencedEnvelope(feature.getRepresentation().getBounds()), feature);
    }

    /**
     * get intersecting features by input feature bounds
     *
     * @param feature input feature
     * @return all intersecting features
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public List<GTVectorFeature> boundsIntersect(@NotNull Feature feature) {
        return this.index.query(new ReferencedEnvelope((Envelope) feature.getBounds()));
    }

    /**
     * get intersecting features by input feature bounds
     *
     * @param feature input feature
     * @return all intersecting features
     */
    @NotNull
    public List<GTVectorFeature> boundsIntersect(@NotNull GTVectorRepresentation feature) {
        return boundsIntersect(feature.resolve());
    }

    /**
     * get intersecting features by input feature bounds
     *
     * @param feature input feature
     * @param buffer  buffer tolerance applied to bounds
     * @return all intersecting features within tolerance
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public List<GTVectorFeature> boundsIntersect(@NotNull Feature feature, double buffer) {
        ReferencedEnvelope envelope = new ReferencedEnvelope((Envelope) feature.getBounds());
        envelope.expandBy(buffer);
        return (List<GTVectorFeature>) this.index.query(envelope);
    }

    /**
     * get intersecting features by input feature bounds
     *
     * @param feature input feature
     * @param buffer  buffer tolerance applied to bounds
     * @return all intersecting features
     */
    @NotNull
    public List<GTVectorFeature> boundsIntersect(@NotNull GTVectorRepresentation feature, double buffer) {
        return boundsIntersect(feature.resolve(), buffer);
    }

}
