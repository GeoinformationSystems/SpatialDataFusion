package de.tudresden.gis.fusion.operation.constraint;

import de.tudresden.gis.fusion.data.IData;

public interface IIODataConstraint extends IConstraint {

	/**
	 * check whether input data complies with constraint
	 * @param input input data
	 * @return true, if constraint is met by input data object
	 */
	public boolean compliantWith(IData input);
	
}
