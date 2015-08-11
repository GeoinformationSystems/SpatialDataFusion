package de.tudresden.gis.fusion.data.feature;

public interface IFeatureRepresentation extends IFeatureView {

	/**
	 * get feature type for this representation
	 * @return related feature type
	 */
	public IFeatureType getType();
	
	/**
	 * get instance represented by this representation
	 * @return related feature instance
	 */
	public IFeatureInstance getInstance();
	
}
