package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IResourceData;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;

public interface IFeature extends IResourceData {

	/**
	 * get concept of this feature
	 * @return feature concept
	 */
	public IFeatureConcept getConcept();
	
	/**
	 * get type of this feature
	 * @return feature type
	 */
	public IFeatureType getType();
	
	/**
	 * get entity of this feature
	 * @return feature entity
	 */
	public IFeatureEntity getEntity();
	
	/**
	 * get representation of this feature
	 * @return feature representation
	 */
	public IFeatureRepresentation getRepresentation();
	
	/**
	 * get all relations attached to this feature
	 * @return all feature relations
	 */
	public Collection<IFeatureRelation> getRelations();
	
}
