package de.tudresden.gis.fusion.data.literal;

import java.util.Arrays;
import java.util.TreeSet;

import de.tudresden.gis.fusion.data.ILiteralData;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.MeasurementRange;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class LongLiteral implements ILiteralData,IMeasurement,ITypedLiteral {

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
	public Long resolve() {
		return value;
	}

	@Override
	public IMeasurementDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		if(measurement instanceof LongLiteral)
			return this.resolve().compareTo(((LongLiteral) measurement).resolve());
		else
			throw new ClassCastException("Cannot cast to LongLiteral");
	}

	@Override
	public String getValue() {
		return String.valueOf(value);
	}
	
	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.LONG.asResource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static MeasurementRange maxRange(){
		return new MeasurementRange(new TreeSet<LongLiteral>(Arrays.asList(new LongLiteral(Long.MIN_VALUE), new LongLiteral(Long.MAX_VALUE))), true);
	}
	
	/**
	 * get positive range for this literal type
	 * @return positive range
	 */
	public static MeasurementRange positiveRange(){
		return new MeasurementRange(new TreeSet<LongLiteral>(Arrays.asList(new LongLiteral(0l), new LongLiteral(Long.MAX_VALUE))), true);
	}
}
