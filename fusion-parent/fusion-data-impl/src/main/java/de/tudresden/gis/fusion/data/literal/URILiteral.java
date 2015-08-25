package de.tudresden.gis.fusion.data.literal;

import java.net.URI;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class URILiteral implements ILiteral,IRDFTypedLiteral {

	private URI value;
	private IDataDescription description;
	
	public URILiteral(URI value, IDataDescription description){
		this.value = value;
		this.description = description;
	}
	
	public URILiteral(URI value){
		this(value, null);
	}

	@Override
	public URI value() {
		return value;
	}

	@Override
	public IDataDescription description() {
		return description;
	}

	@Override
	public ILiteral literalValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource type() {
		return RDFVocabulary.TYPE_ANYURI.resource();
	}

}
