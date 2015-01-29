package de.tudresden.gis.fusion.data.complex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IRelationDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class FeatureRelation implements IResource,IFeatureRelation,IRDFTripleSet {
	
	private IIRI iri;
	private IFeature reference;
	private IFeature target;
	private Collection<IRelationMeasurement> relationMeasurements;
	private IRelationDescription description;
	
	public FeatureRelation(IIRI iri, IFeature reference, IFeature target, Collection<IRelationMeasurement> relationMeasurements, IRelationDescription description){
		this.iri = iri;
		this.reference = reference;
		this.target = target;
		this.relationMeasurements = relationMeasurements;
		this.description = description;
	}
	
	public FeatureRelation(IFeature reference, IFeature target, Collection<IRelationMeasurement> relationMeasurements, IRelationDescription description){
		this(null, reference, target, relationMeasurements, description);
	}
	
	public FeatureRelation(IIRI identifier, IFeature reference, IFeature target, RelationMeasurement relationMeasurement, IRelationDescription description){
		this(reference, target, new ArrayList<IRelationMeasurement>(), description);
		this.addRelationMeasurement(relationMeasurement);
	}
	
	public FeatureRelation(IFeature reference, IFeature target, RelationMeasurement relationMeasurement, IRelationDescription description){
		this(null, reference, target, new ArrayList<IRelationMeasurement>(), description);
		this.addRelationMeasurement(relationMeasurement);
	}
	
	public Map<IIdentifiableResource,INode> getObjectSet(){
		Map<IIdentifiableResource,INode> objectSet = new LinkedHashMap<IIdentifiableResource,INode>();
		objectSet.put(new IdentifiableResource(ERDFNamespaces.INSTANCE_OF.asString()), new IdentifiableResource(EFusionNamespace.RDF_TYPE_FEATURE_RELATION.asString()));
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_REFERENCE.asString()), this.getReference());
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_TARGET.asString()), this.getTarget());
		for(IRelationMeasurement measurement : relationMeasurements){
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_RELATION_MEASUREMENT.asString()), measurement);
		}
		//TODO add metadata
//		objectSet.put(EnumPredicates.HAS_METADATA.getResource(), description);
		return objectSet;
	}

	@Override
	public IFeature getReference() {
		return reference;
	}

	@Override
	public IFeature getTarget() {
		return target;
	}
	
	/**
	 * add relation measurement
	 * @param measurement relation measurement
	 */
	public void addRelationMeasurement(IRelationMeasurement measurement) {
		if(relationMeasurements == null)
			relationMeasurements = new ArrayList<IRelationMeasurement>();
		else
			relationMeasurements.add(measurement);
	}

	@Override
	public boolean isBlank() {
		return getIdentifier() == null;
	}
	
	public boolean isResolvable(){
		return true;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public Collection<IRelationMeasurement> getMeasurements() {
		return relationMeasurements;
	}

	@Override
	public IRelationDescription getDescription() {
		return description;
	}
	
	/**
	 * check if a relation contains a specific relation type measurement
	 * @param type target relation type
	 * @return true, if the relation contains the specified relation type
	 */
	public boolean hasRelationType(IIdentifiableResource type){
		for(IRelationMeasurement measurement : getMeasurements()){
			if(measurement.getRelationType().equals(type))
				return true;
		}
		return false;
	}
	
	@Override
	public IRelationMeasurement getMeasurement(IRelationType type){
		for(IRelationMeasurement measurement : getMeasurements()){
			System.out.println(measurement);
			if(measurement.getRelationType().equals(type))
				return measurement;
		}
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public boolean containsRelationType(IRelationType type) {
		for(IRelationMeasurement measurement : getMeasurements()){
			if(measurement.getRelationType().equals(type))
				return true;
		}
		return false;
	}

}
