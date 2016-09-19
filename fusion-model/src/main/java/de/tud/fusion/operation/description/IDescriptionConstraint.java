package de.tud.fusion.operation.description;

import de.tud.fusion.data.description.IDataDescription;

public interface IDescriptionConstraint {

	/**
	 * check whether description complies with constraint
	 * @param description object to be tested
	 * @return true, if description satisfies constraint
	 */
	public boolean compliantWith(IDataDescription description);
	
}
