package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class BooleanBinding implements ILiteral,IMeasurement<Boolean>,IRDFTypedLiteral {

	private boolean value;
	private IDataDescription description;
	private IRange<Boolean> range;
	
	public BooleanBinding(boolean value, IRange<Boolean> range, IDataDescription description){
		this.value = value;
		this.range = range;
		this.description = description;
	}
	
	public BooleanBinding(boolean value, IDataDescription description){
		this(value, new Range<Boolean>(new Boolean[]{true, false}, false), description);
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(Boolean o) {
		return this.getValue().compareTo(o);
	}

	@Override
	public IRange<Boolean> getRange() {
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
		return RDFVocabulary.TYPE_BOOLEAN.resource();
	}

}
