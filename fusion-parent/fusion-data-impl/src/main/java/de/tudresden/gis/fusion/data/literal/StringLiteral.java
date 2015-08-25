package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class StringLiteral implements ILiteral,IMeasurementValue<String>,IRDFTypedLiteral {

	private String value;
	private IMeasurementDescription description;
	
	public StringLiteral(String value, IMeasurementDescription description){
		this.value = value;
		this.description = description;
	}
	
	public StringLiteral(String value){
		this(value, null);
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public IMeasurementDescription description() {
		return description;
	}

	@Override
	public int compareTo(String o) {
		return this.value().compareTo(o);
	}

	@Override
	public ILiteral literalValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource type() {
		return RDFVocabulary.TYPE_STRING.resource();
	}
}
