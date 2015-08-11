package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class IntegerBinding implements ILiteral,IMeasurement<Integer>,IRDFTypedLiteral {

	private int value;
	private IRDFIdentifiableResource uom;
	private IDataDescription description;
	private IRange<Integer> range;
	
	public IntegerBinding(int value, IRDFIdentifiableResource uom, IRange<Integer> range, IDataDescription description){
		this.value = value;
		this.uom = uom;
		this.range = range;
		this.description = description;
	}
	
	public IntegerBinding(int value, IRDFIdentifiableResource uom, IDataDescription description){
		this(value, uom, new Range<Integer>(new Integer[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, true), description);
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(Integer o) {
		return this.getValue().compareTo(o);
	}

	@Override
	public IRange<Integer> getRange() {
		return range;
	}

	@Override
	public IRDFIdentifiableResource getUnitOfMeasurement() {
		return uom;
	}

	@Override
	public ILiteral getLiteralValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource getType() {
		return RDFVocabulary.TYPE_INTEGER.resource();
	}
}
