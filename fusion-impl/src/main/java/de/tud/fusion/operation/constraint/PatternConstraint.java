package de.tud.fusion.operation.constraint;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.rdf.ILiteral;
import de.tud.fusion.operation.description.IDataConstraint;

/**
 * String pattern constraint
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class PatternConstraint implements IDataConstraint {

	private String pattern;
	
	/**
	 * constructor
	 * @param pattern input pattern
	 */
	public PatternConstraint(String pattern){
		this.pattern = pattern;
	}
	
	@Override
	public boolean compliantWith(IData data) {
		if(data instanceof ILiteral)
			throw new IllegalArgumentException("Pattern constraint is only applicable for literals");
		return ((ILiteral) data).getValue().matches(pattern);
	}
	
}
