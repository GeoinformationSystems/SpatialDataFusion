package de.tud.fusion.operation.description;

import de.tud.fusion.data.IData;

/**
 * Basic data constraint
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IDataConstraint {
	
	/**
	 * check whether data complies with constraint
	 * @param data object to be tested
	 * @return true, if data satisfies constraint
	 */
	public boolean compliantWith(IData data);

}
