package de.tudresden.gis.fusion.data;

import java.net.URI;

/**
 * simple IRI (Internationalized Resourc Identifier) implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class IRI {
	
	private String sIRI;
	private URI uri;
	
	/**
	 * constructor
	 * @param sIri input identifier as String
	 */
	public IRI(String sIRI) {
		this.sIRI = sIRI;
	}
	
	/**
	 * constructor
	 * @param uri input identifier as URI
	 */
	public IRI(URI uri) {
		this.uri = uri;
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
