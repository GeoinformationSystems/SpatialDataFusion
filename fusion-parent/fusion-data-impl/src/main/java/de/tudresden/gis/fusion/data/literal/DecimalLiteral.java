package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class DecimalLiteral implements ILiteral,IMeasurementValue<Double>,IRDFTypedLiteral {

	private double value;
	private IMeasurementDescription description;
	
	public DecimalLiteral(double value, IMeasurementDescription description){
		this.value = value;
		this.description = description;
	}
	
	public DecimalLiteral(double value){
		this(value,	null);
	}

	@Override
	public Double value() {
		return value;
	}

	@Override
	public IMeasurementDescription description() {
		return description;
	}

	@Override
	public int compareTo(Double o) {
		return this.value().compareTo(o);
	}
	
	@Override
	public ILiteral literalValue() {
		return this;
	}

	@Override
	public IRDFIdentifiableResource type() {
		return RDFVocabulary.TYPE_DECIMAL.resource();
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static Range<Double> maxRange(){
		return new Range<Double>(new Double[]{Double.MIN_VALUE, Double.MAX_VALUE}, true);
	}
	
	/**
	 * get positive range for this literal type
	 * @return positive range
	 */
	public static Range<Double> positiveRange(){
		return new Range<Double>(new Double[]{0d, Double.MAX_VALUE}, true);
	}
}
