package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;

public class IRI implements IIRI {
	
	public String sIri;
	
	public IRI(String sIri) {
		this.sIri = sIri;
	}
	
	public IRI(URI uri) {
		this.sIri = uri.toString();
	}

	@Override
	public URI asURI() {
		return URI.create(sIri);
	}

	@Override
	public String asString() {
		return sIri;
	}

	@Override
	public boolean equals(IIRI iri) {
		return this.asString().equals(iri.asString());
	}

}
