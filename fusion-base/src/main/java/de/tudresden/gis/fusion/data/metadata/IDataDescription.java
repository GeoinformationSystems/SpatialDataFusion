package de.tudresden.gis.fusion.data.metadata;

import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

/**
 * basic description for a data object
 * @author Stefan
 *
 */
public interface IDataDescription extends IRDFTripleSet {
	
	/**
	 * textual description of the object
	 * @return description
	 */
	public String getAbstract();

}
