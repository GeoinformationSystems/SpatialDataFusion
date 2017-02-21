package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * feature role implementation
 */
public class Role extends Resource implements IRole {

    /**
     * constructor
     *
     * @param identifier role identifier
     */
    public Role(@NotNull IIdentifier identifier) {
        super(identifier);
    }

    /**
     * constructor
     *
     * @param title role title
     */
    public Role(@NotNull String title) {
        super(title);
    }

    @Override
    public boolean equals(@NotNull Object object) {
        return object instanceof Role && super.equals(object);
    }

}
