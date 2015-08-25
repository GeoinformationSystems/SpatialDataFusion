package de.tudresden.gis.fusion.data.description;

import java.util.List;

import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IDataProvenance {

	/**
	 * get all processes this data has run through
	 * @return process lineage for data object
	 */
	public List<IRDFIdentifiableResource> processLineage();
	
}
