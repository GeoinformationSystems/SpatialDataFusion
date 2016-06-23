package de.tudresden.gis.fusion.data;

import java.net.URI;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * abstract data resource implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractDataResource extends AbstractData implements IResource {
	
	/**
	 * resource identifier
	 */
	private String identifier;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param object data object
	 * @param description data description
	 */
	public AbstractDataResource(String identifier, Object object, IDataDescription description){
		super(object, description);
		this.identifier = identifier;
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param object data object
	 */
	public AbstractDataResource(String identifier, Object object){
		this(identifier, object, null);
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public AbstractDataResource(String identifier){
		this(identifier, null);
	}
	
	@Override
	public URI getURI() {
		return URI.create(identifier);
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public boolean isBlank() {
		return identifier == null || identifier.isEmpty();
	}
	
	public URI relativizeURI(URI uri){
		if(getURI() == null)
			return null;
		if(uri == null)
			return getURI();
		return uri.relativize(getURI());
	}
}
