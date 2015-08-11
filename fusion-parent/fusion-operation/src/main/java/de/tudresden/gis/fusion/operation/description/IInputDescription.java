package de.tudresden.gis.fusion.operation.description;

import de.tudresden.gis.fusion.data.IData;

public interface IInputDescription extends IIODataDescription {

	/**
	 * get default for the io data object
	 * @return default data object, null if none is specified
	 */
	public IData getDefault();
	
}
