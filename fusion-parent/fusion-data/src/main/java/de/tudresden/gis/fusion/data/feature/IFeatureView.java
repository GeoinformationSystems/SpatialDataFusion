package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IFeatureView extends IData,IRDFIdentifiableResource {

	/**
	 * relate feature views to same feature
	 * @param view related view
	 */
	public void link(IFeatureView view);
	
	/**
	 * add relation to other feature views
	 * @param relation relation between the views
	 */
	public void relate(IRelation relation);
	
	/**
	 * get external relations for this view
	 * @return view relations
	 */
	public Collection<IRelation> getRelations();
	
}
