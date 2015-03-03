package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.rdf.IIRI;

public class Resource implements IResource {

	private IIRI iri;
	
	public Resource(IIRI iri){
		this.iri = iri;
	}
	
	public Resource(){
		this(null);
	}

	@Override
	public boolean isBlank() {
		return (iri == null || iri.toString().length() == 0);
	}
	
	/**
	 * set identifier
	 * @param iri identifier
	 */
	protected void setIdentifier(IIRI iri){
		this.iri = iri;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}
	
	/**
	 * create blank resource
	 * @return blank resource
	 */
	public static Resource newEmptyResource(){
		return new Resource(null);
	}
	
}
