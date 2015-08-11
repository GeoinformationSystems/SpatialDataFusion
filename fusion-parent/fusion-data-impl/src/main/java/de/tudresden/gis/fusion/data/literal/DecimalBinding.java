package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class DecimalBinding implements ILiteral,IMeasurement<Double>,IRDFTypedLiteral {

	private double value;
	private IRDFIdentifiableResource uom;
	private IDataDescription description;
	private IRange<Double> range;
	
	public DecimalBinding(double value, IRDFIdentifiableResource uom, IRange<Double> range, IDataDescription description){
		this.value = value;
		this.uom = uom;
		this.range = range;
		this.description = description;
	}
	
	public DecimalBinding(double value, IRDFIdentifiableResource uom, IDataDescription description){
		this(value, uom, new Range<Double>(new Double[]{Double.MIN_VALUE, Double.MAX_VALUE}, true), description);
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(Double o) {
		return this.getValue().compareTo(o);
	}

	@Override
	public IRange<Double> getRange() {
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
		return RDFVocabulary.TYPE_DECIMAL.resource();
	}

}
