package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.ISubject;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;

import java.util.Set;

/**
 * feature implementation
 */
public interface IFeature extends ISubject {

	/**
	 * get concept of this feature
	 * @return feature concept
	 */
    IFeatureConcept getConcept();
	
	/**
	 * get type of this feature
	 * @return feature type
	 */
    IFeatureType getType();
	
	/**
	 * get entity of this feature
	 * @return feature entity
	 */
    IFeatureEntity getEntity();
	
	/**
	 * get representation of this feature
	 * @return feature representation
	 */
    IFeatureRepresentation getRepresentation();
	
	/**
	 * get all relations attached to this feature
	 * @return all feature relations
	 */
    Set<IRelation<? extends IFeature>> getRelations();
	
}
