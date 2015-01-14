package de.tudresden.gis.fusion.data.complex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class FeatureRelationCollection implements IResource,IFeatureRelationCollection {
	
	private IIRI iri;
	private List<IFeatureRelation> relations;
	private IDataDescription description;
	
	public FeatureRelationCollection(){
		initCollection();
	}
	
	public FeatureRelationCollection(IIRI iri, List<IFeatureRelation> relations, IDataDescription description){
		this.iri = iri;
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
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public boolean isBlank() {
		return getIdentifier() == null;
	}
	
	public Map<IIdentifiableResource,INode> getObjectSet(){
		Map<IIdentifiableResource,INode> objectSet = new LinkedHashMap<IIdentifiableResource,INode>();
		for(IFeatureRelation relation : relations){
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_MEMBER.asString()), relation);
		}
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
