package de.tudresden.gis.fusion.data.literal;

import java.util.Arrays;
import java.util.TreeSet;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.MeasurementRange;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

/**
 * decimal literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class DecimalLiteral extends AbstractMeasurement<Double> implements ITypedLiteral {

	/**
	 * constructor
	 * @param value decimal literal value
	 * @param description literal description 
	 */
	public DecimalLiteral(double value, IMeasurementDescription description){
		super(value, description);
	}
	
	/**
	 * constructor
	 * @param value decimal literal value
	 */
	public DecimalLiteral(double value){
		this(value,	null);
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
		return String.valueOf(resolve());
	}
	
	@Override
	public IResource getType() {
		return RDFVocabulary.DECIMAL.getResource();
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
