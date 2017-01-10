package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;

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
