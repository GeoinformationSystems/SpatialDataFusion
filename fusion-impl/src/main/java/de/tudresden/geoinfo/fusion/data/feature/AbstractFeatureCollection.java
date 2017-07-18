package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * generic feature collection implementation
 */
public abstract class AbstractFeatureCollection<T extends AbstractFeature> extends DataCollection<T> implements IFeatureCollection<T> {

    /**
     * constructor
     *
     * @param identifier identifier
     * @param features collection object
     */
    public AbstractFeatureCollection(@NotNull IIdentifier identifier, @NotNull Collection<T> features, @Nullable IMetadata metadata) {
        super(identifier, features, metadata);
    }

}
