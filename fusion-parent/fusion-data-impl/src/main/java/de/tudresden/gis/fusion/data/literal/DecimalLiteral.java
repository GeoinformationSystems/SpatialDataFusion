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

public class DecimalLiteral implements ILiteralData,IMeasurement,ITypedLiteral {

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
	public Double resolve() {
		return value;
	}

	@Override
	public IMeasurementDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		if(measurement instanceof DecimalLiteral)
			return this.resolve().compareTo(((DecimalLiteral) measurement).resolve());
		else
			throw new ClassCastException("Cannot cast to DecimalLiteral");
	}
	
	@Override
	public String getValue() {
		return String.valueOf(value);
	}
	
	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.DECIMAL.asResource();
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static MeasurementRange maxRange(){
		return new MeasurementRange(new TreeSet<DecimalLiteral>(Arrays.asList(new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE))), true);
	}
	
	/**
	 * get positive range for this literal type
	 * @return positive range
	 */
	public static MeasurementRange positiveRange(){
		return new MeasurementRange(new TreeSet<DecimalLiteral>(Arrays.asList(new DecimalLiteral(0d), new DecimalLiteral(Double.MAX_VALUE))), true);
	}
}
