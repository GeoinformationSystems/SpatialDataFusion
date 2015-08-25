package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class LongLiteral implements ILiteral,IMeasurementValue<Long>,IRDFTypedLiteral {

	private long value;
	private IMeasurementDescription description;
	
	public LongLiteral(long value, IMeasurementDescription description){
		this.value = value;
		this.description = description;
	}
	
	public LongLiteral(long value){
		this(value,	null);
	}

	@Override
	public Long value() {
		return value;
	}

	@Override
	public IMeasurementDescription description() {
		return description;
	}

	@Override
	public int compareTo(Long o) {
		return this.value().compareTo(o);
	}

	@Override
	public ILiteral literalValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource type() {
		return resource();
	}
	
	/**
	 * get resource for this literal type
	 * @return literal type resource
	 */
	public static IRDFIdentifiableResource resource(){
		return RDFVocabulary.TYPE_LONG.resource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static Range<Long> maxRange(){
		return new Range<Long>(new Long[]{Long.MIN_VALUE, Long.MAX_VALUE}, true);
	}
	
	/**
	 * get positive range for this literal type
	 * @return positive range
	 */
	public static Range<Long> positiveRange(){
		return new Range<Long>(new Long[]{0l, Long.MAX_VALUE}, true);
	}
}
