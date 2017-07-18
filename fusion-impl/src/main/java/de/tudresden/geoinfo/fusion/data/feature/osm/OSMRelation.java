package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import de.tudresden.geoinfo.fusion.data.relation.IRelationType;
import de.tudresden.geoinfo.fusion.data.relation.IRole;
import de.tudresden.geoinfo.fusion.data.relation.Relation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * OSM relation
 */
public class OSMRelation extends Relation {

    /**
     * constructor
     *
     * @param identifier identifier
     * @param members    relation members with role
     * @param type       relation types
     */
    public OSMRelation(@NotNull IIdentifier identifier, @NotNull IRelationType type, @NotNull Map<IRole, Set<OSMVectorFeature>> members, @Nullable IMetadata metadata) {
        super(identifier, type, new HashMap<>(), metadata);
        for(Map.Entry<IRole, Set<OSMVectorFeature>> entry : members.entrySet()){
            Set<IRDFResource> resources = new HashSet<>();
            resources.addAll(entry.getValue());
            this.addMembers(entry.getKey(), resources);
        }
    }

}