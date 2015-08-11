package de.tudresden.gis.fusion.data.description;

public interface IDataDescription extends IResourceDescription {
	
	/**
	 * get provenance for this data object (http://purl.org/dc/terms/provenance)
	 * @return data provenance
	 */
	public IDataProvenance getProvenance();
	
}
