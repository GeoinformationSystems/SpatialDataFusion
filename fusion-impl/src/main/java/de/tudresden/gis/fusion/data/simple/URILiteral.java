package de.tudresden.gis.fusion.data.simple;

import java.net.URI;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;

public class URILiteral extends Literal {
	
	public URILiteral(String value){
		super(value.toString());
	}
	
	public URILiteral(URI value){
		this(value.toString());
	}
	
	public String getProtocol(){
		return URI.create(getIdentifier()).getScheme();
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_ANYURI.resource();
	}
	
}
