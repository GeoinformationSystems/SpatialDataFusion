package de.tudresden.gis.fusion.data.geotools;

import java.util.ArrayList;
import java.util.Collection;

import org.opengis.feature.type.FeatureType;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.ITypeRelation;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;

public class GTFeatureType extends RDFIdentifiableResource implements IFeatureType {
	
	private FeatureType type;
	private Collection<IFeatureRepresentation> representations;
	private IFeatureConcept concept;
	
	private Collection<IRelation> relations;
	
	public GTFeatureType(IRI identifier, FeatureType type){
		super(identifier);
		this.type = type;
	}
	
	public GTFeatureType(FeatureType type){
		this(new IRI(type.getName().toString()), type);
	}
	
	/**
	 * get GeoTools feature type
	 * @return feature type
	 */
	public FeatureType getValue(){
		return type;
	}

	@Override
	public void link(IFeatureView view) {
		if(view instanceof IFeatureConcept)
			this.concept = (IFeatureConcept) view;
		else if(view instanceof IFeatureRepresentation){
			if(representations == null)
				representations = new ArrayList<IFeatureRepresentation>();
			representations.add((IFeatureRepresentation) view);
		}			
		//else: do nothing
	}

	@Override
	public IFeatureConcept getConcept() {
		return concept;
	}

	@Override
	public Collection<IFeatureRepresentation> getRepresentations() {
		return representations;
	}

	@Override
	public void relate(IRelation relation) {
		if(relations == null)
			relations = new ArrayList<IRelation>();
		if(relation instanceof ITypeRelation)
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
