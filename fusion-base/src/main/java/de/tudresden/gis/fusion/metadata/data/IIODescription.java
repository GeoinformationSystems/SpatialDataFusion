package de.tudresden.gis.fusion.metadata.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

/**
 * description for operation IO data
 * @author Stefan
 *
 */
public interface IIODescription extends IDescription {
	
	/**
	 * get identifier for operation IO
	 * @return IO identifier
	 */
	public String getIdentifier();
	
	/**
	 * get default for the IO data
	 * @return default object or null, if not defined
	 */
	public IData getDefault();
	
	/**
	 * get io restrictions
	 * @return io restrictions
	 */
	public Collection<IIORestriction> getDataRestrictions();
	
}
