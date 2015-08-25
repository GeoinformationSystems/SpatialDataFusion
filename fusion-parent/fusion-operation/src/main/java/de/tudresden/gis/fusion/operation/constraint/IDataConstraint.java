package de.tudresden.gis.fusion.operation.constraint;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;

public interface IDataConstraint {
	
	/**
	 * check whether data complies with constraint
	 * @param data map of data objects
	 * @return true, if constraint is met by data
	 */
	public boolean compliantWith(Map<String,IData> data);

}
