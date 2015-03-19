package de.tudresden.gis.fusion.data.complex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
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

public class FeatureRelationCollection extends Resource implements IRDFTripleSet,IRDFCollection,IFeatureRelationCollection {
	
	private List<IFeatureRelation> relations;
	private IDescription description;
	
	public FeatureRelationCollection(){
		initCollection();
	}
	
	public FeatureRelationCollection(IIRI iri, List<IFeatureRelation> relations, IDescription description){
		super(iri);
		this.relations = relations;
		this.description = description;
	}
	
	public FeatureRelationCollection(FeatureRelation relation){
		initCollection();
		this.addRelation(relation);
	}
	
	public void addRelation(IFeatureRelation relation){
		relations.add(relation);
	}
	
	private void initCollection(){
		relations = new ArrayList<IFeatureRelation>();
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

	@Override
	public boolean isBlank() {
		return getIdentifier() == null;
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

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

	@Override
	public Collection<IFeatureRelation> getRelations() {
		return relations;
	}
}
