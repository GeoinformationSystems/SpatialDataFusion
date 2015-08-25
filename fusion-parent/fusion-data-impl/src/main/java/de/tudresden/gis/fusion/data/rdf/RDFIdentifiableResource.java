package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.IRI;

public class RDFIdentifiableResource implements IRDFIdentifiableResource {

	private IRI identifier;
	
	public RDFIdentifiableResource(IRI identifier){
		this.identifier = identifier;
	}
	
	public RDFIdentifiableResource(String identifier){
		this(new IRI(identifier));
	}

	@Override
	public IRI identifier() {
		return identifier;
	}

}
