package de.tudresden.gis.fusion.data.simple;

import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class RelationType extends IdentifiableResource implements IRelationType {

	public RelationType(IIRI value) {
		super(value);
	}

	@Override
	public boolean equals(IRelationType type) {
		return this.getIdentifier().equals(type.getIdentifier());
	}

	@Override
	public boolean isSubtypeOf(IRelationType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupertypeOf(IRelationType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public IDataDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
