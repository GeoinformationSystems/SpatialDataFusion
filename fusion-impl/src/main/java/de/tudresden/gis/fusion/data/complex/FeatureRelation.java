package de.tudresden.gis.fusion.data.complex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.metadata.IRelationDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;

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
	
	public FeatureRelation(IRDFTripleSet decodedRDFResource) throws IOException {
		
		//check if subject is set
		if(!decodedRDFResource.getSubject().isBlank())
			this.iri = decodedRDFResource.getSubject().getIdentifier();
		
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		
		//set reference (mandatory)
		Set<INode> referenceSet = objectSet.get(EFusionNamespace.HAS_REFERENCE.resource());
		if(referenceSet == null || referenceSet.size() != 1)
			throw new IOException("Missing or multiple reference resource for feature relation.");
		INode referenceNode = referenceSet.iterator().next();
		if(!(referenceNode instanceof IResource))
			throw new IOException("Invalid reference resource for feature relation.");
		this.reference = new FeatureReference(((IResource) referenceNode).getIdentifier());
		
		//set target (mandatory)
		Set<INode> targetSet = objectSet.get(EFusionNamespace.HAS_TARGET.resource());
		if(targetSet == null || targetSet.size() != 1)
			throw new IOException("Missing or multiple target resource for feature relation.");
		INode targetNode = targetSet.iterator().next();
		if(!(targetNode instanceof IResource))
			throw new IOException("Invalid target resource for feature relation.");
		this.target = new FeatureReference(((IResource) targetNode).getIdentifier());
		
		//set relation measurements (optional)
		Set<INode> measurementSet = objectSet.get(EFusionNamespace.HAS_RELATION_MEASUREMENT.resource());
		if(measurementSet == null)
			return;
		for(INode node : measurementSet){
			IRelationMeasurement measurement = getMeasurement(node);
			if(measurement != null)
				this.addRelationMeasurement(measurement);
		}
	}
	
	private IRelationMeasurement getMeasurement(INode node){
		
		if(!(node instanceof IRDFTripleSet))
			return null;
		
		//check if subject is set
		IIRI iri = null;
		if(!((IRDFTripleSet) node).getSubject().isBlank())
			iri = ((IRDFTripleSet) node).getSubject().getIdentifier();
		
		//get object set		
		Map<IIdentifiableResource,Set<INode>> objectSet = ((IRDFTripleSet) node).getObjectSet();
		Set<INode> typeSet = objectSet.get(EFusionNamespace.HAS_RELATION_TYPE.resource());
		Set<INode> valueSet = objectSet.get(ERDFNamespaces.HAS_VALUE.resource());
		if(typeSet == null || typeSet.size() != 1 || valueSet == null || valueSet.size() != 1)
			return null;
		
		IMeasurementDescription description = getMeasurementDescription(typeSet.iterator().next());
		IMeasurementValue<?> value = getMeasurementValue(valueSet.iterator().next());
		
		if(description == null || value == null)
			return null;
		
		IIRI type = (IIRI) objectSet.get(ERDFNamespaces.INSTANCE_OF.resource()).iterator().next().getIdentifier();
		
		if(type.equals(EFusionNamespace.RDF_TYPE_SIMILARITY_MEASUREMENT.resource().getIdentifier()))
			return new SimilarityMeasurement(iri, value, description);
		else if(type.equals(EFusionNamespace.RDF_TYPE_RELATION_MEASUREMENT.resource().getIdentifier()))
			return new RelationMeasurement(iri, value, description);
		
		return null;
	}
	
	private IMeasurementDescription getMeasurementDescription(INode node){
		//TODO implement full description
		IRelationType relationType = new RelationType(((IResource) node).getIdentifier());
		return new MeasurementDescription(null, null, relationType, null);
	}
	
	private IMeasurementValue<?> getMeasurementValue(INode node){
		return (IMeasurementValue<?>) node;
	}

	public Map<IIdentifiableResource,Set<INode>> getObjectSet(){
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(EFusionNamespace.RDF_TYPE_FEATURE_RELATION.resource()));
		objectSet.put(EFusionNamespace.HAS_REFERENCE.resource(), DataUtilities.toSet(this.getReference()));
		objectSet.put(EFusionNamespace.HAS_TARGET.resource(), DataUtilities.toSet(this.getTarget()));
		objectSet.put(EFusionNamespace.HAS_RELATION_MEASUREMENT.resource(), DataUtilities.collectionToSet(relationMeasurements));
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
