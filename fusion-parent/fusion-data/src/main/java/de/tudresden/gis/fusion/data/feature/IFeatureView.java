package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IFeatureView extends IData,IRDFIdentifiableResource {

	/**
	 * relate feature views internally
	 * @param view related view
	 */
	public void link(IFeatureView view);
	
	/**
	 * get internal links for this view
	 * @return internal view relations
	 */
	public Collection<IFeatureView> featureLinks();
	
	/**
	 * add relation to external feature views
	 * @param relation relation between feature views
	 */
	public void relate(IRelation<IFeatureView> relation);
	
	/**
	 * get external relations for this view
	 * @return external view relations
	 */
	public Collection<IRelation<IFeatureView>> featureRelations();
	
}
