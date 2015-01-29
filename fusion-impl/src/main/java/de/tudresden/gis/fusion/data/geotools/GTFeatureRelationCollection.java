package de.tudresden.gis.fusion.data.geotools;

import java.util.ArrayList;
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
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.manage.DataUtilities;

/**
 * relation collection backed by collection of features to reduce redundant feature storage
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeatureRelationCollection implements IResource,IFeatureRelationCollection {
	
	private IIRI iri;
	private IDataDescription description;
	
	private Map<String,FeatureReference> referenceFeatures;
	private Map<String,FeatureReference> targetFeatures;
	
	private List<IFeatureRelation> relations;
	
	public GTFeatureRelationCollection(){
		init();
	}
	
	public GTFeatureRelationCollection(IIRI iri, List<IFeatureRelation> relations, IDataDescription description){
		this.iri = iri;
		this.description = description;
		init();
		rebuildRelations(relations);
	}
	
	public GTFeatureRelationCollection(IFeatureRelation relation){
		init();
		this.addRelation(relation);
	}
	
	private void rebuildRelations(List<IFeatureRelation> relations) {
		for(IFeatureRelation relation : relations){
			this.addRelation(relation);
		}
	}
	
	public void addRelation(IFeatureRelation relation){
		if(!referenceFeatures.containsKey(relation.getReference().getIdentifier().asString()))
			referenceFeatures.put(relation.getReference().getIdentifier().asString(), new FeatureReference(relation.getReference().getIdentifier()));
		if(!targetFeatures.containsKey(relation.getTarget().getIdentifier().asString()))
			targetFeatures.put(relation.getTarget().getIdentifier().asString(), new FeatureReference(relation.getTarget().getIdentifier()));
		relations.add(new FeatureRelation(
				relation.getIdentifier(),
				referenceFeatures.get(relation.getReference().getIdentifier().asString()),
				targetFeatures.get(relation.getTarget().getIdentifier().asString()), 
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
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public boolean isBlank() {
		return getIdentifier() == null;
	}
	
	public Map<IIdentifiableResource,Set<INode>> getObjectSet(){
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(EFusionNamespace.HAS_MEMBER.resource(), DataUtilities.collectionToSet(relations));
		return objectSet;
	}
	
	public boolean isResolvable(){
		return true;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public Iterator<IFeatureRelation> iterator() {
		return relations.iterator();
	}

	@Override
	public IResource getSubject() {
		return this;
	}
	
	@Override
	public List<? extends IRDFRepresentation> getRDFCollection() {
		return relations;
	}
}
