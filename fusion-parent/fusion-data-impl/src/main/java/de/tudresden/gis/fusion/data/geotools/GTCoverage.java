package de.tudresden.gis.fusion.data.geotools;

import java.util.ArrayList;
import java.util.Collection;

import org.opengis.coverage.Coverage;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureInstance;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRepresentationRelation;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;

public class GTCoverage extends RDFIdentifiableResource implements IFeatureRepresentation {

	private Coverage coverage;
	private IFeatureType type;
	private IFeatureInstance instance;
	
	private Collection<IRelation> relations;
	
	public GTCoverage(IRI identifier, Coverage coverage){
		super(identifier);
		this.coverage = coverage;
	}
	
	public GTCoverage(Coverage coverage){
		this(new IRI(coverage.toString()), coverage);
	}
	
	/**
	 * get GeoTools coverage representation
	 * @return coverage representation
	 */
	public Coverage getValue() {
		return coverage;
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
