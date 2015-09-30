package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;

public class Resource implements IResource {
	
	private String identifier;
	private transient URI uri;
	
	public Resource(String identifier){
		this.identifier = identifier;
	}

	@Override
	public URI asURI() {
		if(uri == null)
			uri = URI.create(identifier);
		return uri;
	}
	
	@Override
	public boolean isBlank() {
		return identifier == null || identifier.isEmpty();
	}
	
	public String asString(){
		return identifier;
	}
	
	@Override
	public URI relativizeURI(URI uri){
		if(asURI() == null)
			return null;
		if(uri == null)
			return asURI();
		return uri.relativize(asURI());
	}
	
}
