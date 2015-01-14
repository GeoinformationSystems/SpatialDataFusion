package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class IdentifiableResource extends Resource implements IIdentifiableResource {
	
	public IdentifiableResource(IIRI iri){
		super(iri);
		if(isBlank())
			throw new IllegalArgumentException("iri must not be null @" + this.getClass());
	}
	
	public IdentifiableResource(String sIri){
		this(new IRI(sIri));
	}

}
