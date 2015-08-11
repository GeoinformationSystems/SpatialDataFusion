package de.tudresden.gis.fusion.data.geotools;

import java.util.ArrayList;
import java.util.Collection;

import org.opengis.feature.Feature;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureInstance;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRepresentationRelation;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;

public class GTFeature extends RDFIdentifiableResource implements IFeatureRepresentation {

	private Feature feature;
	private IFeatureType type;
	private IFeatureInstance instance;
	
	private Collection<IRelation> relations;
	
	public GTFeature(IRI identifier, Feature feature){
		super(identifier);
		this.feature = feature;
	}
	
	public GTFeature(Feature feature){
		this(new IRI(feature.getIdentifier().getID()), feature);
	}
	
	/**
	 * get GeoTools feature representation
	 * @return feature representation
	 */
	public Feature getValue(){
		return feature;
	}
	
	@Override
	public IFeatureType getType() {
		return type;
	}

	@Override
	public IFeatureInstance getInstance() {
		return instance;
	}

	@Override
	public void link(IFeatureView view) {
		if(view instanceof IFeatureType)
			this.type = (IFeatureType) view;
		else if(view instanceof IFeatureInstance)
			this.instance = (IFeatureInstance) view;
		//else: do nothing
	}

	@Override
	public void relate(IRelation relation) {
		if(relations == null)
			relations = new ArrayList<IRelation>();
		if(relation instanceof IRepresentationRelation)
			relations.add(relation);
		//else: do nothing
	}

	@Override
	public Collection<IRelation> getRelations() {
		return relations;
	}

	@Override
	public IDataDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
