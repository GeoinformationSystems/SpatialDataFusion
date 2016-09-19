package de.tud.fusion.data.literal;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.ILiteralData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * Boolean literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class Literal<T> extends ResourceData implements ILiteralData {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param value literal value
	 * @param description literal measurement description
	 */
	public Literal(String identifier, T value, IDataDescription description) {
		super(identifier, value, description);
	}

	@Override
	public String getValue() {
		return String.valueOf(resolve());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T resolve() {
		return (T) super.resolve();
	}
	
}
