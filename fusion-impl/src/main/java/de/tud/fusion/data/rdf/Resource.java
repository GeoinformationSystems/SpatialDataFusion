package de.tud.fusion.data.rdf;

import java.net.URI;
import java.util.UUID;

import de.tud.fusion.data.IdentifiableObject;

/**
 * RDF resource implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Resource extends IdentifiableObject implements IResource {
	
	/**
	 * resource uri
	 */
	private URI uri;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public Resource(String identifier){
		super(identifier);
	}

	@Override
	public URI getURI() {
		if(uri == null)
			uri = URI.create(getIdentifier());
		return uri;
	}
	
	@Override
	public boolean isBlank() {
		return getIdentifier() == null || getIdentifier().isEmpty();
	}
	
	/**
	 * make this resource an identifiable resource, if identifier is blank, a UUID is generated
	 */
	protected void makeIdentifiable(){
		if(this.isBlank())
			this.uri = URI.create(UUID.randomUUID().toString());
	}
	
	/**
	 * relativize resource URI
	 * @param uriBase URI base (omitted from result)
	 * @return URI relative to uriBase
	 */
	public URI relativizeURI(URI uriBase){
		if(getURI() == null)
			return null;
		if(uriBase == null)
			return getURI();
		return uriBase.relativize(getURI());
	}
	
}
