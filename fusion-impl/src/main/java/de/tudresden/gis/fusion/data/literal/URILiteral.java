package de.tudresden.gis.fusion.data.literal;

import java.net.URI;

import de.tudresden.gis.fusion.data.ILiteralData;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class URILiteral implements ILiteralData,ITypedLiteral {

	/**
	 * literal URI
	 */
	private URI value;
	
	/**
	 * literal description
	 */
	private IDataDescription description;
	
	/**
	 * constructor
	 * @param value URI literal value
	 * @param description literal description 
	 */
	public URILiteral(URI value, IDataDescription description){
		this.value = value;
		this.description = description;
	}
	
	/**
	 * constructor
	 * @param value URI literal value
	 */
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
	public IResource getType() {
		return RDFVocabulary.ANYURI.getResource();
	}
	
	@Override
	public String toString(){
		return getValue();
	}

}
