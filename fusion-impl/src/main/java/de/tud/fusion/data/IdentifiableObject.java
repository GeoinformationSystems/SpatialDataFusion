package de.tud.fusion.data;

import java.util.UUID;

public class IdentifiableObject implements IIdentifiableObject {
	
	/**
	 * identifier
	 */
	private String identifier;
	
	/**
	 * constructor
	 * @param identifier object identifier
	 */
	public IdentifiableObject(String identifier){
		this.identifier = identifier != null ? identifier : UUID.randomUUID().toString();
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * check, if two resources are equal
	 * @param resource resource to compare
	 * @return true, if both resource strings are equal
	 */
	public boolean equals(Object resource){
		return resource instanceof IIdentifiableObject && this.getIdentifier().equals(((IIdentifiableObject) resource).getIdentifier());
	}
}
