package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class IntegerLiteral implements ILiteral,IMeasurementValue<Integer>,IRDFTypedLiteral {

	private int value;
	private IMeasurementDescription description;
	
	public IntegerLiteral(int value, IMeasurementDescription description){
		this.value = value;
		this.description = description;
	}
	
	public IntegerLiteral(int value){
		this(value,	null);
	}

	@Override
	public Integer value() {
		return value;
	}

	@Override
	public IMeasurementDescription description() {
		return description;
	}

	@Override
	public int compareTo(Integer o) {
		return this.value().compareTo(o);
	}

	@Override
	public ILiteral literalValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource type() {
		return RDFVocabulary.TYPE_INTEGER.resource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static Range<Integer> maxRange(){
		return new Range<Integer>(new Integer[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, true);
	}
	
	/**
	 * get positive range for this literal type
	 * @return positive range
	 */
	public static Range<Integer> positiveRange(){
		return new Range<Integer>(new Integer[]{0, Integer.MAX_VALUE}, true);
	}
}
