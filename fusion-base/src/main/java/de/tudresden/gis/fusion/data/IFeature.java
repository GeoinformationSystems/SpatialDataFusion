package de.tudresden.gis.fusion.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.metadata.data.IFeatureDescription;

/**
 * basic feature implementation
 * @author Stefan
 *
 */
public interface IFeature extends IComplexData {
	
	/**
	 * get feature identifier
	 * @return feature identifier
	 */
	public String getFeatureId();
	
	/**
	 * get spatial properties for feature
	 * @return spatial properties
	 */
	public Collection<ISpatialProperty> getSpatialProperties();
	
	/**
	 * get default spatial property
	 * @return default spatial property
	 */
	public ISpatialProperty getDefaultSpatialProperty();
	
	/**
	 * get thematic properties for feature
	 * @return thematic properties
	 */
	public Collection<IThematicProperty> getThematicProperties();
	
	/**
	 * get temporal properties for feature
	 * @return temporal properties
	 */
	public Collection<ITemporalProperty> getTemporalProperties();
	
	/**
	 * get feature property by identifier
	 * @param identifier property identifier
	 * @return feature property
	 */
	public IFeatureProperty getFeatureProperty(String identifier);
	
	@Override
	public IFeatureDescription getDescription();
	
}
