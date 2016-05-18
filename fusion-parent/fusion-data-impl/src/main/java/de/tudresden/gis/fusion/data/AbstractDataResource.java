package de.tudresden.gis.fusion.data;

import java.net.URI;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;

public abstract class AbstractDataResource extends AbstractData implements IResource {
	
	private String identifier;
	
	public AbstractDataResource(String identifier, Object object, IDataDescription description){
		super(object, description);
		this.identifier = identifier;
	}
	
	public AbstractDataResource(String identifier, Object object){
		super(object);
		this.identifier = identifier;
	}

	public AbstractDataResource(String identifier){
		this(identifier, null);
	}
	
	@Override
	public URI asURI() {
		return URI.create(identifier);
	}
	
	@Override
	public String identifier() {
		return identifier;
	}
	
	@Override
	public boolean isBlank() {
		return identifier == null || identifier.isEmpty();
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
