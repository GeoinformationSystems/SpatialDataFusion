package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

/**
 * feature role implementation
 */
public class Role extends Resource implements IRole {

    /**
     * constructor
     * @param identifier role identifier
     */
    public Role(IIdentifier identifier){
        super(identifier);
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Role && super.equals(object);
    }

}
