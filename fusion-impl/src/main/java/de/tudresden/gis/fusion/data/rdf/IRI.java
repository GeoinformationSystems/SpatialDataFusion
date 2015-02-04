package de.tudresden.gis.fusion.data.rdf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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
	public URL asURL() {
		try {
			return asURI().toURL();
		} catch (MalformedURLException me){
			//if no protocol is provided, try file resource
			try {
				return new File(sIri).toURI().toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		}
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
