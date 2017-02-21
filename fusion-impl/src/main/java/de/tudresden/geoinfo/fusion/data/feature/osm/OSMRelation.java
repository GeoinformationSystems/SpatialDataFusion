package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelationType;
import de.tudresden.geoinfo.fusion.data.relation.IRole;
import de.tudresden.geoinfo.fusion.data.relation.Relation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * OSM relation
 */
public class OSMRelation extends Relation<OSMVectorFeature> {

    /**
     * constructor
     *
     * @param identifier resource identifier
     * @param members    relation members with role
     * @param type       relation types
     */
    public OSMRelation(@Nullable IIdentifier identifier, @NotNull IRelationType type, @NotNull Map<IRole, Set<OSMVectorFeature>> members, @Nullable IMetadata metadata) {
        super(identifier, type, members, metadata);
    }

}