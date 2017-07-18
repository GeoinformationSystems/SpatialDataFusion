package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * abstract observation instance
 * TODO implement observation
 */
public abstract class AbstractObservation<T> extends AbstractFeature<T> implements IObservation {

    /**
     * constructor
     *
     * @param identifier identifier
     * @param observation observation object
     * @param relations   observation relations
     */
    public AbstractObservation(@NotNull IIdentifier identifier, @NotNull T observation, @Nullable IMetadata metadata, @Nullable Set<IRelation> relations) {
        super(identifier, observation, metadata, relations);
    }

}
