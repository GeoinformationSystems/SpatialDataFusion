package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;

public enum ERDFNamespaces {

	//RDF literal types
	LITERAL_TYPE_BOOLEAN("http://www.w3.org/TR/xmlschema11-2/#boolean"),
	LITERAL_TYPE_INTEGER("http://www.w3.org/TR/xmlschema11-2/#integer"),
	LITERAL_TYPE_LONG("http://www.w3.org/TR/xmlschema11-2/#long"),
	LITERAL_TYPE_DECIMAL("http://www.w3.org/TR/xmlschema11-2/#decimal"),
	LITERAL_TYPE_STRING("http://www.w3.org/TR/xmlschema11-2/#string"),
	
	//RDF basics
	INSTANCE_OF("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
	HAS_VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
	
	private String sUri;
	private ERDFNamespaces(String sURI){
		this.sUri = sURI;
	}
	public URI asURI(){
		return URI.create(asString());
	}
	public String asString(){
		return sUri;
	}
	
}
