package de.tud.fusion.operation.constraint;

import de.tud.fusion.data.IData;
import de.tud.fusion.operation.description.IDataConstraint;

/**
 * Mandatory constraint
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class MandatoryConstraint implements IDataConstraint {

	@Override
	public boolean compliantWith(IData data) {
		return data != null;
	}

}
