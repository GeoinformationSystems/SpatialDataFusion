package de.tudresden.gis.fusion.data.rdf;

public class IdentifiableResource extends Resource implements IIdentifiableResource {

	public IdentifiableResource(String identifier) {
		super(identifier);
		if(identifier == null || identifier.isEmpty())
			throw new IllegalArgumentException("IdentifiableResource must not be null or empty");
	}
	
	public IdentifiableResource(IResource resource) {
		this(resource.identifier());
	}
	
	@Override
	public boolean isBlank() {
		return false;
	}

}
