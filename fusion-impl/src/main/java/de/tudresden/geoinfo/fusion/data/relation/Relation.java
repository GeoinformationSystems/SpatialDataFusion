package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.DataSubject;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * feature relation implementation
 */
public class Relation extends DataSubject<Map<IRole,Set<IRDFResource>>> implements IRelation {

    private IRelationType type;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param type       relation type
     * @param members    relation members with role
     */
    public Relation(@NotNull IIdentifier identifier, @NotNull IRelationType type, @NotNull Map<IRole,Set<IRDFResource>> members, @Nullable IMetadata metadata) {
        super(identifier, new HashMap<>(), metadata);
        for(Map.Entry<IRole, Set<IRDFResource>> entry : members.entrySet()){
            this.addMembers(entry.getKey(), entry.getValue());
        }
        this.setRelationType(type);
    }

    /**
     * set relation member
     *
     * @param role   member roles
     * @param members member resources
     */
    protected void addMembers(IRole role, Set<IRDFResource> members) {
        for(IRDFResource member : members){
            this.addMember(role, member);
        }
    }

    /**
     * set relation member
     *
     * @param role   member role
     * @param member member resource
     */
    protected void addMember(IRole role, IRDFResource member) {
        this.getMembers(role).add(member);
        this.addObject(role, member);
    }

    @NotNull
    @Override
    public Set<IRDFResource> getMembers() {
        Set<IRDFResource> members = new HashSet<>();
        for (Set<IRDFResource> member : this.resolve().values()) {
            members.addAll(member);
        }
        return members;
    }

    @NotNull
    @Override
    public Set<IRDFResource> getMembers(@NotNull IRole role) {
        return this.resolve().get(role) != null ? this.resolve().get(role) : new HashSet<>();
    }

    private void setRelationType(IRelationType type) {
        this.type = type;
        this.addObject(Predicates.RELATION_TYPE.getResource(), this.type);
    }

    @NotNull
    @Override
    public IRelationType getRelationType() {
        return this.type;
    }

}
