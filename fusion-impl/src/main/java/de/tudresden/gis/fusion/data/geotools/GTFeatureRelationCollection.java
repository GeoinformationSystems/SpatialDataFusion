package de.tudresden.gis.fusion.data.geotools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureReference;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IDescription;

/**
 * relation collection backed by collection of features to reduce redundant feature storage
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeatureRelationCollection extends Resource implements IRDFTripleSet,IRDFCollection,IFeatureRelationCollection {
	
	private IDescription description;
	
	private Map<String,FeatureReference> referenceFeatures;
	private Map<String,FeatureReference> targetFeatures;
	
	private List<IFeatureRelation> relations;
	
	public GTFeatureRelationCollection(){
		init();
	}
	
	public GTFeatureRelationCollection(IIRI iri, List<IFeatureRelation> relations, IDescription description){
		super(iri);
		this.description = description;
		init();
		rebuildRelations(relations);
	}
	
	public GTFeatureRelationCollection(IFeatureRelation relation){
		super();
		init();
		this.addRelation(relation);
	}
	
	private void rebuildRelations(List<IFeatureRelation> relations) {
		for(IFeatureRelation relation : relations){
			this.addRelation(relation);
		}
	}
	
	public void addRelation(IFeatureRelation relation){
		if(!referenceFeatures.containsKey(relation.getReference().getFeatureId()))
			referenceFeatures.put(relation.getReference().getFeatureId(), new FeatureReference(relation.getReference().getFeatureId()));
		if(!targetFeatures.containsKey(relation.getTarget().getFeatureId()))
			targetFeatures.put(relation.getTarget().getFeatureId(), new FeatureReference(relation.getTarget().getFeatureId()));
		relations.add(new FeatureRelation(
				relation.getRDFRepresentation().getSubject().getIdentifier(),
				referenceFeatures.get(relation.getReference().getRDFRepresentation().getSubject().getIdentifier().toString()),
				targetFeatures.get(relation.getTarget().getRDFRepresentation().getSubject().getIdentifier().toString()), 
				relation.getMeasurements(), 
				relation.getDescription()));
	}
	
	private void init(){
		relations = new ArrayList<IFeatureRelation>();
		referenceFeatures = new HashMap<String,FeatureReference>();
		targetFeatures = new HashMap<String,FeatureReference>();
	}
	
	public boolean isEmpty(){
		return relations.isEmpty();
	}
	
	public int size(){
		return relations.size();
	}

	@Override
	public IDescription getDescription() {
		return description;
	}

	public Map<IIdentifiableResource,Set<INode>> getObjectSet(){
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(EFusionNamespace.HAS_MEMBER.resource(), DataUtilities.dataCollectionToNodeSet(relations));
		return objectSet;
	}
	
	public boolean isResolvable(){
		return true;
	}

	@Override
	public Iterator<IFeatureRelation> iterator() {
		return relations.iterator();
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

	@Override
	public Collection<IFeatureRelation> getRelations() {
		return relations;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public Collection<? extends IRDFRepresentation> getRDFCollection() {
		Collection<IRDFRepresentation> collection = new ArrayList<IRDFRepresentation>();
		for(IFeatureRelation relation : relations){
			collection.add(relation.getRDFRepresentation());
		}
		return collection;
	}
}
