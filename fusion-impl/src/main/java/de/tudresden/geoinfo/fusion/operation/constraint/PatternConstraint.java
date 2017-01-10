package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.ILiteral;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;

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
			return ((ILiteral) data).getValue().matches(pattern);
		return false;
	}
	
}
