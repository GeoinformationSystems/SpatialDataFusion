package de.tudresden.gis.fusion.operation.io;

import java.util.Set;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IDescription;

/**
 * restrictions for data
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IIORestriction extends IDescription {
	
	/**
	 * get classification for restriction
	 * @return restriction classifications
	 */
	public Set<IIdentifiableResource> getClassification();

	/**
	 * check whether input data complies with restriction
	 * @param input input data
	 * @return true, if restriction is met by input data
	 */
	public boolean compliantWith(IData input);
	
}
