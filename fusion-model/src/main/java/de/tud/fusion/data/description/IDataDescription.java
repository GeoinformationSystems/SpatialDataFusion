package de.tud.fusion.data.description;

import de.tud.fusion.data.rdf.IResource;

/**
 * Description of a data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IDataDescription extends IDescription {
	
	/**
	 * get measurement type
	 * @return measurement type
	 */
	public IResource getDataType();
	
}
