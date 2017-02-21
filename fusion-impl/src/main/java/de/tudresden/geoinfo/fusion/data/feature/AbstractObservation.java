package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * abstract observation instance
 * TODO implement observation
 */
public abstract class AbstractObservation extends AbstractFeature implements IObservation {

    /**
     * constructor
     *
     * @param identifier  observation identifier
     * @param observation observation object
     * @param relations   observation relations
     */
    public AbstractObservation(@Nullable IIdentifier identifier, @NotNull Object observation, @Nullable IMetadata metadata, @Nullable Set<IRelation<? extends IFeature>> relations) {
        super(identifier, observation, metadata, relations);
        // TODO Auto-generated constructor stub
    }

}
