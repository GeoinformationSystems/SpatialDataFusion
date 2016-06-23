package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;
import java.util.UUID;

/**
 * RDF resource implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Resource implements IResource {
	
	/**
	 * string identifier for this resource
	 */
	private String identifier;
	
	/**
	 * URI for this resource
	 */
	private transient URI uri;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param identifiable determine, if the resource must be identifiable
	 */
	public Resource(String identifier, boolean identifiable){
		this.identifier = identifier;
		if(identifiable && this.isBlank())
			this.makeIdentifiable();
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public Resource(String identifier){
		this(identifier, false);
	}

	@Override
	public URI getURI() {
		if(uri == null)
			uri = URI.create(identifier);
		return uri;
	}
	
	@Override
	public boolean isBlank() {
		return identifier == null || identifier.isEmpty();
	}
	
	@Override
	public String getIdentifier(){
		return identifier;
	}
	
	/**
	 * make this resource an identifiable resource, if identifier is blank, a UUID is generated
	 */
	public void makeIdentifiable(){
		if(this.isBlank())
			this.uri = URI.create(UUID.randomUUID().toString());
	}
	
	/**
	 * relativize URI
	 * @param uri path to omit from URI
	 * @return relativized URI
	 */
	public URI relativizeURI(URI uri){
		if(getURI() == null)
			return null;
		if(uri == null)
			return getURI();
		return uri.relativize(getURI());
	}
	
	/**
	 * check, if two resources are equal
	 * @param resource resource to compare
	 * @return true, if both resource strings are equal
	 */
	public boolean equals(Object resource){
		return resource instanceof IResource && this.getIdentifier().equals(((IResource) resource).getIdentifier());
	}
	
}
