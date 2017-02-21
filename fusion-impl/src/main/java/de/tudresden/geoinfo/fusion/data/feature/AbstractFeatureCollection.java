package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * generic feature collection implementation
 */
public abstract class AbstractFeatureCollection<T extends AbstractFeature> extends DataCollection<T> implements IFeatureCollection<T> {

    /**
     * map of features
     */
    private transient Map<IIdentifier, T> featureMap;

    /**
     * constructor
     *
     * @param identifier        collection identifier
     * @param featureCollection collection object
     */
    public AbstractFeatureCollection(@Nullable IIdentifier identifier, @NotNull Collection<T> featureCollection, @Nullable IMetadata metadata) {
        super(identifier, featureCollection, metadata);
    }

    /**
     * get size of collection
     *
     * @return number of features in collection
     */
    public int size() {
        return this.resolve().size();
    }

    /**
     * get feature by id
     *
     * @param identifier feature id
     * @return feature with specified id or null, if no such feature exists
     */
    @Nullable
    public T getFeatureById(@NotNull IIdentifier identifier) {
        if (featureMap == null)
            initFeatureMap();
        return featureMap.get(identifier);
    }

    /**
     * get feature by id
     *
     * @param sIdentifier feature id as string
     * @return feature with specified id or null, if no such feature exists
     */
    @Nullable
    public T getFeatureById(@NotNull String sIdentifier) {
        if (featureMap == null)
            initFeatureMap();
        for (T feature : this.resolve()) {
            if (feature.getIdentifier().toString().equals(sIdentifier))
                return feature;
        }
        return null;
    }

    /**
     * check if collection contains specified feature
     *
     * @param identifier feature identifier
     * @return true, if collection contains feature with specified id
     */
    public boolean containsId(@NotNull IIdentifier identifier) {
        return featureMap.containsKey(identifier);
    }

    /**
     * add a feature
     *
     * @param feature input feature
     */
    public void add(@NotNull T feature) {
        this.resolve().add(feature);
        if (featureMap == null)
            initFeatureMap();
        else {
            featureMap.put(feature.getIdentifier(), feature);
        }
    }

    /**
     * initialize feature map
     */
    private void initFeatureMap() {
        featureMap = new HashMap<>();
        for (T feature : this.resolve()) {
            featureMap.put(feature.getIdentifier(), feature);
        }
    }

}
