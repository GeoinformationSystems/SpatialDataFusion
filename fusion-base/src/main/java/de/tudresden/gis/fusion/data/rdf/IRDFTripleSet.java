package de.tudresden.gis.fusion.data.rdf;

import java.util.Map;
import java.util.Set;

public interface IRDFTripleSet extends IRDFRepresentation {
	
	public Map<IIdentifiableResource,Set<INode>> getObjectSet();
	
}
