package de.tud.fusion.data.literal;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.description.IMeasurementRange;
import de.tud.fusion.data.description.MeasurementRange;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * Boolean literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class BooleanLiteral extends Literal<Boolean> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param value literal value
	 * @param description literal measurement description
	 */
	public BooleanLiteral(String identifier, boolean value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean value
	 */
	public BooleanLiteral(boolean value) {
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.BOOLEAN.getResource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static IMeasurementRange getMaxRange(){
		return new MeasurementRange<Boolean>(new Measurement[]{getMeasurement(true), getMeasurement(false)}, false);
	}
	
	/**
	 * get measurement instance from boolean value
	 * @param value boolean value
	 * @return measurement instance
	 */
	public static Measurement<Boolean> getMeasurement(boolean value){
		return new Measurement<Boolean>(new BooleanLiteral(null, value, null));
	}
	
}
