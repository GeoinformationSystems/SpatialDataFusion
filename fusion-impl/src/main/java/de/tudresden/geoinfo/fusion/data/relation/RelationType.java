package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.RDFProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Relation type implementation
 */
public class RelationType extends RDFProperty implements IRelationType {

    private Collection<IRole> roles;

    /**
     * constructor
     *
     * @param identifier type resource identifier
     * @param roles      roles of the type
     */
    public RelationType(@NotNull String identifier, @NotNull Collection<IRole> roles) {
        super(identifier);
        this.roles = roles;
    }

    @NotNull
    @Override
    public Collection<IRole> getRoles() {
        return this.roles;
    }

}
