package de.tud.fusion.data.feature;

import java.util.Set;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.rdf.IResource;

public interface IFeature extends IResource,IData {

	/**
	 * get concept of this feature
	 * @return feature concept
	 */
	public IFeatureConceptView getConcept();
	
	/**
	 * get type of this feature
	 * @return feature type
	 */
	public IFeatureTypeView getType();
	
	/**
	 * get entity of this feature
	 * @return feature entity
	 */
	public IFeatureEntityView getEntity();
	
	/**
	 * get representation of this feature
	 * @return feature representation
	 */
	public IFeatureRepresentationView getRepresentation();
	
	/**
	 * get all relations attached to this feature
	 * @return all feature relations
	 */
	public Set<IFeatureRelation> getRelations();
	
}
