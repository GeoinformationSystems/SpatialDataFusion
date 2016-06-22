package de.tudresden.gis.fusion.operation.constraint;

import de.tudresden.gis.fusion.data.IData;

public interface IDataConstraint {
	
	/**
	 * check whether data complies with constraint
	 * @param data object
	 * @return true, if constraint is satisfied
	 */
	public boolean compliantWith(IData data);

}
