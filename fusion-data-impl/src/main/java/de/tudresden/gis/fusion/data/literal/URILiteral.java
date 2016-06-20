package de.tudresden.gis.fusion.data.literal;

import java.net.URI;

import de.tudresden.gis.fusion.data.ILiteralData;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class URILiteral implements ILiteralData,ITypedLiteral {

	private URI value;
	private IDataDescription description;
	
	public URILiteral(URI value, IDataDescription description){
		this.value = value;
		this.description = description;
	}
	
	public URILiteral(URI value){
		this(value, null);
	}

	public URILiteral(String string) {
		this(URI.create(string));
	}

	@Override
	public URI resolve() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public String getValue() {
		return String.valueOf(value);
	}
	
	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.ANYURI.asResource();
	}
	
	@Override
	public String toString(){
		return getValue();
	}

}
