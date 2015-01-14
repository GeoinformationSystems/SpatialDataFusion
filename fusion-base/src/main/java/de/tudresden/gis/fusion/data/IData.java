package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.metadata.IDataDescription;

/**
 * data binding
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IData {

	/**
	 * get data description
	 * @return data description
	 */
	public IDataDescription getDescription();
	
}
