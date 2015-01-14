package de.tudresden.gis.fusion.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.metadata.IFeatureDescription;

/**
 * basic feature implementation
 * @author Stefan
 *
 */
public interface IFeature extends IComplexData {
	
	public Collection<ISpatialProperty> getSpatialProperties();
	
	/**
	 * retrieve default spatial property
	 * @return default spatial property
	 */
	public ISpatialProperty getDefaultSpatialProperty();
	
	public Collection<IThematicProperty> getThematicProperties();
	
	public Collection<ITemporalProperty> getTemporalProperties();
	
	public IFeatureProperty getFeatureProperty(String identifier);
	
	@Override
	public IFeatureDescription getDescription();
	
}
