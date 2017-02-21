package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * feature relation implementation
 */
public class Relation<T extends IResource> extends Data<Map<IRole, Set<T>>> implements IRelation<T> {

    private IRelationType type;

    /**
     * constructor
     *
     * @param identifier resource identifier
     * @param type       relation type
     * @param members    relation members with role
     */
    public Relation(@Nullable IIdentifier identifier, @NotNull IRelationType type, @NotNull Map<IRole, Set<T>> members, @Nullable IMetadata metadata) {
        super(identifier, members, metadata);
        this.type = type;
    }

    /**
     * set relation member
     *
     * @param role   member roles
     * @param member member resource
     */
    protected void setMember(IRole role, Set<T> member) {
        this.getMembers(role).addAll(member);
    }

    /**
     * set relation member
     *
     * @param role   member role
     * @param member member resource
     */
    protected void setMember(IRole role, T member) {
        this.getMembers(role).add(member);
    }

    @NotNull
    @Override
    public Set<T> getMembers() {
        Set<T> members = new HashSet<>();
        for (Set<T> member : this.resolve().values()) {
            members.addAll(member);
        }
        return members;
    }

    @NotNull
    @Override
    public Set<T> getMembers(@NotNull IRole role) {
        if (!this.type.getRoles().contains(role))
            throw new IllegalArgumentException("Relation does not contain role " + role.getIdentifier());
        return this.resolve().get(role) != null ? this.resolve().get(role) : new HashSet<>();
    }

    @NotNull
    @Override
    public IRelationType getRelationType() {
        return this.type;
    }

}
