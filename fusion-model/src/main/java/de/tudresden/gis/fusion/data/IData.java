package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IDataDescription;

/**
 * basic data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IData {
	
	/**
	 * resolve data object or value
	 * @return Java object represented by this resource
	 */
	public Object resolve();
	
	/**
	 * get description of this object or value
	 * @return resource description
	 */
	public IDataDescription getDescription();

}
