package de.tudresden.gis.fusion.manage;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public enum ERelationType {
	
	//related vs. unrelated
	RELATED("#related"),
	UNRELATED("#unrelated"),
	
	//conceptual relation
	
	//representational relation
	
	//schema relation
	
	//property relation
	
	//spatial property relation
	LOCATION("/related/representation/property/spatial#location"),
	GEOMETRY("/related/representation/property/spatial#geometry"),
	TOPOLOGY("/related/representation/property/spatial#topology"),
	
	//specific geometry relation types
	ORIENTATION_DIFF("/related/representation/property/spatial/geometry/orientation#difference"),
	
	;

	private IIdentifiableResource resource;
	
	private ERelationType(String identifier){
		this.resource = new IdentifiableResource(Namespace.uri_process() + identifier);
	}
	
	public IIdentifiableResource resource(){
		return resource;
	}
	
}
