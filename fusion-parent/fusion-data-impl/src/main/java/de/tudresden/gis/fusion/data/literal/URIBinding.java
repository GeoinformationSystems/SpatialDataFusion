package de.tudresden.gis.fusion.data.literal;

import java.net.URI;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class URIBinding implements ILiteral,IRDFTypedLiteral {

	private URI value;
	private IDataDescription description;
	
	public URIBinding(URI value, IDataDescription description){
		this.value = value;
		this.description = description;
	}

	@Override
	public URI getValue() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public ILiteral getLiteralValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource getType() {
		return RDFVocabulary.TYPE_ANYURI.resource();
	}

}
