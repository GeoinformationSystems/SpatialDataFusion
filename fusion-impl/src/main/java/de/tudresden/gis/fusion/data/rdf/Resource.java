package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;

public class Resource implements IDataResource {

	private IIRI iri;
	
	public Resource(IIRI iri){
		this.iri = iri;
	}
	
	public Resource(){
		this(null);
	}

	@Override
	public boolean isBlank() {
		return (iri == null || iri.asString().length() == 0);
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}
	
	/**
	 * get blank resource
	 * @return blank resource
	 */
	public static Resource newEmptyResource(){
		return new Resource(null);
	}

	@Override
	public IDataDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
