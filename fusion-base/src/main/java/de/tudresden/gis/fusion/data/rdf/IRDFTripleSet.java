package de.tudresden.gis.fusion.data.rdf;

import java.util.Map;

public interface IRDFTripleSet extends IRDFRepresentation {
	
	public Map<IIdentifiableResource,INode> getObjectSet();
	
}
