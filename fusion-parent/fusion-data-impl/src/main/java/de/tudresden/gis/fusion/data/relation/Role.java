package de.tudresden.gis.fusion.data.relation;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.relation.IRole;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;

public class Role extends RDFIdentifiableResource implements IRole {

	public Role(IRI identifier){
		super(identifier);
	}

}
