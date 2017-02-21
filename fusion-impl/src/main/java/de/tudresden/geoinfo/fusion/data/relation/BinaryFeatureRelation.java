package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeature;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * feature relation implementation
 */
public class BinaryFeatureRelation extends BinaryRelation<AbstractFeature> {

    /**
     * constructor
     *
     * @param identifier   resource identifier
     * @param domain       relation domain
     * @param range        relation range
     * @param type         relation types
     * @param measurements relation measurements
     */
    public BinaryFeatureRelation(@Nullable IIdentifier identifier, @NotNull AbstractFeature domain, @NotNull AbstractFeature range, @NotNull IBinaryRelationType type, @Nullable Set<IRelationMeasurement> measurements, @Nullable IMetadata metadata) {
        super(identifier, domain, range, type, measurements, metadata);
    }

}
