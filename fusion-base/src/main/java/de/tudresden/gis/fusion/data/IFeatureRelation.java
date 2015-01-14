package de.tudresden.gis.fusion.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.metadata.IRelationDescription;

/**
 * feature relation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureRelation extends IComplexData {

	/**
	 * get reference feature object
	 * @return reference feature object
	 */
	public IFeature getReference();
	
	/**
	 * get target feature object
	 * @return target feature object
	 */
	public IFeature getTarget();
	
	/**
	 * get relation measurements for this relation
	 * @return relation measurements
	 */
	public Collection<IRelationMeasurement> getMeasurements();
	
	public boolean containsRelationType(IRelationType type);
	
	public IRelationMeasurement getMeasurement(IRelationType type);
	
	public void addRelationMeasurement(IRelationMeasurement measurement);
	
	@Override
	public IRelationDescription getDescription();
	
}
