package de.tud.fusion.data.literal;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.description.MeasurementRange;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * Boolean literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class LongLiteral extends Literal<Long> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public LongLiteral(String identifier, long value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public LongLiteral(long value){
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.LONG.getResource();
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static MeasurementRange<Long> getMaxRange(){
		return new MeasurementRange<Long>(new Measurement[]{getMeasurement(Long.MIN_VALUE), getMeasurement(Long.MAX_VALUE)}, false);
	}
	
	/**
	 * get positive range for this literal type
	 * @return maximum range
	 */
	@SuppressWarnings("unchecked")
	public static MeasurementRange<Long> getPositiveRange(){
		return new MeasurementRange<Long>(new Measurement[]{getMeasurement(0), getMeasurement(Long.MAX_VALUE)}, false);
	}
	
	/**
	 * get measurement instance from long value
	 * @param value long value
	 * @return measurement instance
	 */
	public static Measurement<Long> getMeasurement(long value){
		return new Measurement<Long>(new LongLiteral(null, value, null));
	}
	
}
