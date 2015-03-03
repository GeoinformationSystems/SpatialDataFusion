package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;
import java.net.URL;

public interface IIRI {

	public URI asURI();
	
	public String toString();
	
	public boolean equals(IIRI iri);

	public URL asURL();
	
}
