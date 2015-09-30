package de.tudresden.gis.fusion.data.feature.relation;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IResource;

public interface IFeatureRelation extends IData,IResource,IRelation<IFeature> {

	/**
	 * get feature view identifier for this relation
	 * @return feature view identifier
	 */
	public IIdentifiableResource getFeatureView();
	
}
