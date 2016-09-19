package de.tud.fusion.data.literal;

import de.tud.fusion.data.IMeasurement;
import de.tud.fusion.data.description.IMeasurementDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

public class Measurement<T extends Comparable<T>> extends Literal<T> implements IMeasurement {

	/**
	 * constructor
	 * @param identifier measurement id
	 * @param value measurement value
	 * @param description measurement description
	 */
	public Measurement(String identifier, T value, IMeasurementDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param literal input literal value
	 */
	public Measurement(Literal<T> literal) {
		this(literal.getIdentifier(), literal.resolve(), null);
	}

	@Override
	public IMeasurementDescription getDescription() {
		return (IMeasurementDescription) super.getDescription();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(IMeasurement target) {
		if(!(target.getClass().isAssignableFrom(resolve().getClass())))
			throw new IllegalArgumentException("Cannot compare " + this.getType().getIdentifier() + " with " + target.getType().getIdentifier());
		return resolve().compareTo((T) target.resolve());
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.MEASUREMENT.getResource();
	}

}
