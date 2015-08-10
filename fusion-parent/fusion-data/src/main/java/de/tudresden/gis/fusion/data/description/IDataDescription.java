package de.tudresden.gis.fusion.data.description;

public interface IDataDescription {

	/**
	 * get Java binding for this type
	 * @return Java binding
	 */
	public Class<?> getJavaBinding();
	
	/**
	 * get provenance for this data object
	 * @return data provenance
	 */
	public IDataProvenance getProvenance();
	
}
