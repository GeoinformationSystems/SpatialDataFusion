package de.tudresden.gis.fusion.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.feature.ISpatialProperty;

/**
 * feature collection
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureCollection extends IComplexData,Iterable<IFeature> {

	/**
	 * get collection identifier
	 * @return collection identifier
	 */
	public String getCollectionId();
	
	/**
	 * get number of features
	 * @return number of features
	 */
	public int size();
	
	/**
	 * get feature collection
	 * @return features
	 */
	public Collection<IFeature> getFeatures();
	
	/**
	 * get feature by identifier
	 * @param featureIRI identifier
	 * @return feature with identifier or null, if no feature was found for specified idnetifier
	 */
	public IFeature getFeatureById(String featureId);
	
	/**
	 * get spatial property for feature collection
	 * @return spatial property
	 */
	public ISpatialProperty getSpatialProperty();
	
}
