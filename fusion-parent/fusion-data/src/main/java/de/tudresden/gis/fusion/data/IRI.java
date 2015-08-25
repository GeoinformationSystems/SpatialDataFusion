package de.tudresden.gis.fusion.data;

import java.net.URI;
import java.net.URL;

public class IRI {
	
	private String sIRI;
	private URI uri;
	
	public IRI(String sIRI) {
		this.sIRI = sIRI;
	}
	
	public IRI(URI uri) {
		this.uri = uri;
	}

	public IRI(URL url) {
		this(url.toString());
	}

	/**
	 * Java URI representation of the IRI
	 * @return Java URI
	 */
	public URI toURI(){
		return uri != null ? uri : URI.create(sIRI);
	}
	
	/**
	 * Java String representation of the IRI
	 * @return Java URI
	 */
	public String toString(){
		return sIRI != null ? sIRI : uri.toString();
	}
	
	/**
	 * check if this IRI equals the provided IRI
	 * @param iri provided IRI
	 * @return true, if both IRI are equal, false otherwise
	 */
	public boolean equals(IRI iri){
		return this.toString().equals(iri.toString());
	}
	
}
