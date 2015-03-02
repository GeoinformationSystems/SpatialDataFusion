package de.tudresden.gis.fusion.data.feature;

/**
 * feature property
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureProperty {
	
	/**
	 * get value for this property
	 * @return property value
	 */
	public Object getValue();
	
	/**
	 * get property identifier
	 * @return property identifier
	 */
	public String getIdentifier();
	
	/**
	 * get JAVA binding for property value
	 * @return value JAVA binding
	 */
	public Class<?> getJavaBinding();

}
