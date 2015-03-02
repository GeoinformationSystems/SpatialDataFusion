package de.tudresden.gis.fusion.data.rdf;

import java.util.Collection;

public interface IRDFCollection extends IRDFRepresentation {

	public Collection<? extends IRDFRepresentation> getRDFCollection();
	
}
