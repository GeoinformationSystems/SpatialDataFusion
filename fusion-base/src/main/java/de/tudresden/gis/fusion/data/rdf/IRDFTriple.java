package de.tudresden.gis.fusion.data.rdf;

public interface IRDFTriple extends IRDFRepresentation {
	
	public IIdentifiableResource getPredicate();
	
	public INode getObject();
	
}
