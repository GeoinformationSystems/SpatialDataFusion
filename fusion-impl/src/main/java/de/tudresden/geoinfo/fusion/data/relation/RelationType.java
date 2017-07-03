package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Relation type implementation
 */
public class RelationType extends Resource implements IRelationType {

    private Collection<IRole> roles;

    /**
     * constructor
     *
     * @param identifier type identifier
     * @param roles      roles of the type
     */
    public RelationType(@Nullable IIdentifier identifier, @NotNull Collection<IRole> roles) {
        super(identifier);
        this.roles = roles;
    }

    @NotNull
    @Override
    public Collection<IRole> getRoles() {
        return this.roles;
    }

}
