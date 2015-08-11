package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Collection;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;

public interface IRelation extends IRDFResource {
	
	/**
	 * get reference view of the relation
	 * @return reference feature view
	 */
	public IFeatureView getSource();
	
	/**
	 * get target view of the relation
	 * @return target feature view
	 */
	public IFeatureView getTarget();

	/**
	 * get relation types for this relation
	 * @return relation types
	 */
	public Collection<IRelationType> getRelationTypes();
	
	/**
	 * get relation measurements for this relation
	 * @return relation measurements
	 */
	public Collection<IRelationMeasurement<?>> getRelationMeasurements();
	
}
