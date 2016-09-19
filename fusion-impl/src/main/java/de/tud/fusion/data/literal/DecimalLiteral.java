package de.tud.fusion.data.literal;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.description.MeasurementRange;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * Decimal literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class DecimalLiteral extends Literal<Double> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public DecimalLiteral(String identifier, double value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value literal value
	 */
	public DecimalLiteral(double value){
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.DECIMAL.getResource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static MeasurementRange<Double> getMaxRange(){
		return new MeasurementRange<Double>(new Measurement[]{getMeasurement(Double.MIN_VALUE), getMeasurement(Double.MAX_VALUE)}, false);
	}
	
	/**
	 * get positive range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static MeasurementRange<Double> getPositiveRange(){
		return new MeasurementRange<Double>(new Measurement[]{getMeasurement(0d), getMeasurement(Double.MAX_VALUE)}, false);
	}
	
	/**
	 * get measurement instance from double value
	 * @param value double value
	 * @return measurement instance
	 */
	public static Measurement<Double> getMeasurement(double value){
		return new Measurement<Double>(new DecimalLiteral(null, value, null));
	}
	
}
