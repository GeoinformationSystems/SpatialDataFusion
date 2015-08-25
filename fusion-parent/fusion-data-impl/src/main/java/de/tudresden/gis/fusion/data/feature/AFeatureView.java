package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;

public abstract class AFeatureView extends RDFIdentifiableResource implements IFeatureView {
	
	private Object object;
	private Collection<IFeatureView> links;
	private Collection<IRelation<IFeatureView>> relations;
	private IDataDescription description;
	
	public AFeatureView(IRI identifier, Object object, IDataDescription description){
		super(identifier);
		this.object = object;
		this.description = description;
	}
	
	public AFeatureView(Object object){
		this(new IRI(object.toString()), object, null);
	}

	@Override
	public Object value() {
		return object;
	}

	@Override
	public IDataDescription description() {
		return description;
	}

	@Override
	public void link(IFeatureView view) {
		if(links == null)
			links = new HashSet<IFeatureView>();
		this.links.add(view);
	}
	
	@Override
	public Collection<IFeatureView> featureLinks() {
		return links;
	}

	@Override
	public void relate(IRelation<IFeatureView> relation) {
		if(relations == null)
			relations = new HashSet<IRelation<IFeatureView>>();
		this.relations.add(relation);
	}

	@Override
	public Collection<IRelation<IFeatureView>> featureRelations() {
		return relations;
	}
	
	/**
	 * set object value
	 * @param object object value
	 */
	protected void setValue(Object object){
		this.object = object;
	}

}
