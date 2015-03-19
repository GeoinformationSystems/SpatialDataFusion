package de.tudresden.gis.fusion.data.complex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IDescription;
import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;

public class FeatureRelation extends Resource implements IFeatureRelation,IRDFTripleSet {
	
	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_FEATURE_RELATION.resource();
	private final IIdentifiableResource REFERENCE = EFusionNamespace.RELATION_HAS_REFERENCE.resource();
	private final IIdentifiableResource TARGET = EFusionNamespace.RELATION_HAS_TARGET.resource();
	private final IIdentifiableResource MEASUREMENTS = EFusionNamespace.RELATION_HAS_RELATION_MEASUREMENT.resource();
	
	private IFeature reference;
	private IFeature target;
	private Collection<IRelationMeasurement> relationMeasurements;
	private IDescription description;
	
	public FeatureRelation(IIRI iri, IFeature reference, IFeature target, Collection<IRelationMeasurement> relationMeasurements, IDescription description){
		super(iri);
		this.reference = reference;
		this.target = target;
		this.relationMeasurements = relationMeasurements;
		this.description = description;
	}
	
	public FeatureRelation(IFeature reference, IFeature target, Collection<IRelationMeasurement> relationMeasurements, IRelationMeasurementDescription description){
		this(null, reference, target, relationMeasurements, description);
	}
	
	public FeatureRelation(IIRI identifier, IFeature reference, IFeature target, IRelationMeasurement relationMeasurement, IRelationMeasurementDescription description){
		this(reference, target, new ArrayList<IRelationMeasurement>(), description);
		this.addRelationMeasurement(relationMeasurement);
	}
	
	public FeatureRelation(IFeature reference, IFeature target, IRelationMeasurement relationMeasurement, IRelationMeasurementDescription description){
		this(null, reference, target, new ArrayList<IRelationMeasurement>(), description);
		this.addRelationMeasurement(relationMeasurement);
	}
	
	public FeatureRelation(IRDFTripleSet decodedRDFResource) throws IOException {
		//set iri
		super(decodedRDFResource.getSubject().getIdentifier());
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		//set reference
		INode nReference = DataUtilities.getSingleFromObjectSet(objectSet, REFERENCE, IIdentifiableResource.class, true);
		this.reference = new FeatureReference(((IIdentifiableResource) nReference).getIdentifier());
		//set target
		INode nTarget = DataUtilities.getSingleFromObjectSet(objectSet, TARGET, IIdentifiableResource.class, true);
		this.target = new FeatureReference(((IIdentifiableResource) nTarget).getIdentifier());
		//set measurements
		Set<INode> measurements = DataUtilities.getMultipleFromObjectSet(objectSet, MEASUREMENTS, IRDFTripleSet.class, false);
		if(measurements != null) {
			this.relationMeasurements = new ArrayList<IRelationMeasurement>();
			for(INode measurement : measurements){
				this.relationMeasurements.add(getMeasurement((IRDFTripleSet) measurement));
			}
		}
	}
	
	private IRelationMeasurement getMeasurement(IRDFTripleSet measurement) throws IOException {
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = measurement.getObjectSet();
		//get measurement type
		IIdentifiableResource type = (IIdentifiableResource) DataUtilities.getSingleFromObjectSet(objectSet, ERDFNamespaces.INSTANCE_OF.resource(), IIdentifiableResource.class, true);
		//return measurement
		if(type.getIdentifier().equals(EFusionNamespace.RDF_TYPE_SIMILARITY_MEASUREMENT.resource().getIdentifier()))
			return new SimilarityMeasurement(measurement);
		else if(type.getIdentifier().equals(EFusionNamespace.RDF_TYPE_CONFIDENCE_MEASUREMENT.resource().getIdentifier()))
			return new ConfidenceMeasurement(measurement);
		else if(type.getIdentifier().equals(EFusionNamespace.RDF_TYPE_RELATION_MEASUREMENT.resource().getIdentifier()))
			return new RelationMeasurement(measurement);		
		return null;
	}

	public Map<IIdentifiableResource,Set<INode>> getObjectSet(){
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		objectSet.put(REFERENCE, DataUtilities.toSet(this.getReference().getRDFRepresentation()));
		objectSet.put(TARGET, DataUtilities.toSet(this.getTarget().getRDFRepresentation()));
		objectSet.put(MEASUREMENTS, DataUtilities.dataCollectionToNodeSet(relationMeasurements));
		//TODO add metadata
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

	public boolean isResolvable(){
		return true;
	}

	@Override
	public Collection<IRelationMeasurement> getMeasurements() {
		return relationMeasurements;
	}

	@Override
	public IDescription getDescription() {
		return description;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

	@Override
	public boolean containsRelationMeasurement(IIdentifiableResource classification) {
		for(IRelationMeasurement measurement : getMeasurements()){
			for(IIdentifiableResource res : measurement.getDescription().getRelationTypes())
				if(res.getIdentifier().equals(classification.getIdentifier()))
					return true;
		}
		return false;
	}

	@Override
	public IRelationMeasurement getRelationMeasurement(IIdentifiableResource classification) {
		for(IRelationMeasurement measurement : getMeasurements()){
			for(IIdentifiableResource res : measurement.getDescription().getRelationTypes())
				if(res.getIdentifier().equals(classification.getIdentifier()))
					return measurement;
		}
		return null;
	}

}
