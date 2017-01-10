package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Collection;

/**
 * Relation type implementation
 */
public class RelationType extends Subject implements IRelationType {

	private Collection<IRole> roles;

	/**
	 * constructor
	 * @param identifier type identifier
	 * @param metadata relation type metadata
	 */
	public RelationType(IIdentifier identifier, Collection<IRole> roles, IMetadataForData metadata) {
		super(identifier, roles, metadata);
	}

    @SuppressWarnings("unchecked")
    @Override
    public Collection<IRole> getRoles() {
        return (Collection<IRole>) super.resolve();
    }
}
