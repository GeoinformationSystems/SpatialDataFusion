package de.tud.fusion.data;

import de.tud.fusion.data.description.IDataDescription;

/**
 * Basic data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IData extends IIdentifiableObject {

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
