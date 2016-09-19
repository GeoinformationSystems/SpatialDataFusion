package de.tud.fusion.data.literal;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.description.MeasurementRange;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * Integer literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class IntegerLiteral extends Literal<Integer> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public IntegerLiteral(String identifier, int value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public IntegerLiteral(int value){
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.INTEGER.getResource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static MeasurementRange<Integer> getMaxRange(){
		return new MeasurementRange<Integer>(new Measurement[]{getMeasurement(Integer.MIN_VALUE), getMeasurement(Integer.MAX_VALUE)}, false);
	}
	
	/**
	 * get positive range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static MeasurementRange<Integer> getPositiveRange(){
		return new MeasurementRange<Integer>(new Measurement[]{getMeasurement(0), getMeasurement(Integer.MAX_VALUE)}, false);
	}
	
	/**
	 * get measurement instance from integer value
	 * @param value integer value
	 * @return measurement instance
	 */
	public static Measurement<Integer> getMeasurement(int value){
		return new Measurement<Integer>(new IntegerLiteral(null, value, null));
	}
	
}
