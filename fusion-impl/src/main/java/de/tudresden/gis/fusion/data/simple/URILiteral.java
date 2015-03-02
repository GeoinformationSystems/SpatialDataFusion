package de.tudresden.gis.fusion.data.simple;

import java.net.URI;

import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class URILiteral extends Literal {
	
	public URILiteral(String value){
		super(value.toString());
	}
	
	public URILiteral(URI value){
		this(value.toString());
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_ANYURI.resource();
	}
	
}
