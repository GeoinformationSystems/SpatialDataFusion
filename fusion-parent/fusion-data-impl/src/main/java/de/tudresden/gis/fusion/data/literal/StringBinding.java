package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class StringBinding implements ILiteral,IMeasurement<String>,IRDFTypedLiteral {

	private String value;
	private IDataDescription description;
	private IRange<String> range;
	
	public StringBinding(String value, IRange<String> range, IDataDescription description){
		this.value = value;
		this.range = range;
		this.description = description;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(String o) {
		return this.getValue().compareTo(o);
	}

	@Override
	public IRange<String> getRange() {
		return range;
	}

	@Override
	public IRDFIdentifiableResource getUnitOfMeasurement() {
		return null;
	}

	@Override
	public ILiteral getLiteralValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource getType() {
		return RDFVocabulary.TYPE_STRING.resource();
	}
}
