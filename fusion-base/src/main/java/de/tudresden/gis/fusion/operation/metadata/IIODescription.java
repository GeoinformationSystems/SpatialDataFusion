package de.tudresden.gis.fusion.operation.metadata;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;

/**
 * description for operation IO data
 * @author Stefan
 *
 */
public interface IIODescription extends IDataDescription {
	
	/**
	 * get io identifier
	 * @return identifier
	 */
	public IIRI getIdentifier();
	
	/**
	 * get default for the IO data
	 * @return default object or null, if not defined
	 */
	public IData getDefault();
	
	/**
	 * get io restrictions
	 * @return io restrictions
	 */
	public Collection<IDataRestriction> getDataRestrictions();
	
}
