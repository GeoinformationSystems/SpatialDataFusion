package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.RDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import org.jetbrains.annotations.NotNull;

/**
 * feature role implementation
 */
public class Role extends RDFProperty implements IRole {

    /**
     * constructor
     *
     * @param identifier role resource identifier
     */
    public Role(@NotNull String identifier) {
        super(identifier);
    }

    /**
     * constructor
     *
     * @param predicate role resource predicate
     */
    public Role(@NotNull IRDFProperty predicate) {
        super(predicate.getIRI());
    }

}
