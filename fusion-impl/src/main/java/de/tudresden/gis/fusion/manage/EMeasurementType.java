package de.tudresden.gis.fusion.manage;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public enum EMeasurementType {

	//arithmetic measurements
	SUM("/numeric#sum"),
	DIFFERENCE("/numeric#difference"),
	
	//string measurements
	STRING_DIST("/string#distance"),
	
	//geometric measurements
	GEOM_DIST_EUC("/geometry/distance#euclidean"),
	GEOM_DIST_NEUC("/geometry/distance#non-euclidean"),
	GEOM_TOP_DIST("/geometry/topology#distance"),
	GEOM_TOP_DIFF("/geometry/topology#difference"),
	GEOM_SHAPE_DIFF("/geometry/shape#difference"),
	
	//topology measurements
	TOPO_OVERLAP("/topology#overlap"),
	TOPO_INTERSECT("/topology#intersect"),
	
	;
	
	private IIdentifiableResource resource;
	
	private EMeasurementType(String identifier){
		this.resource = new IdentifiableResource(Namespace.uri_measurement() + identifier);
	}
	
	public IIdentifiableResource resource(){
		return resource;
	}
	
}
