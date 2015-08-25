package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class FeatureRelationCollection extends RDFResource implements IRDFCollection,IRDFTripleSet,IDataCollection<IRelation<IFeatureView>> {

	private final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns";
	private Collection<IRelation<IFeatureView>> relations;
	
	public FeatureRelationCollection(IRI identifier, Collection<IRelation<IFeatureView>> relations) {
		super(identifier);
		this.relations = relations;
	}
	
	public FeatureRelationCollection(IRI identifier) {
		this(identifier, new HashSet<IRelation<IFeatureView>>());
	}
	
	public FeatureRelationCollection() {
		this(null);
	}
	
	@Override
	public void add(IRelation<IFeatureView> relation){
		relations.add(relation);
	}

	@Override
	public IRDFResource subject() {
		return this;
	}

	@Override
	public Object value() {
		return relations;
	}

	@Override
	public IDataDescription description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return relations.size();
	}

	@Override
	public Collection<IRelation<IFeatureView>> collection() {
		return relations;
	}

	@Override
	public Iterator<IRelation<IFeatureView>> iterator() {
		return relations.iterator();
	}

	@Override
	public IRelation<IFeatureView> elementById(IRI identifier) {
		Iterator<IRelation<IFeatureView>> iterator = this.iterator();
	      while(iterator.hasNext()) {
	    	  IRelation<IFeatureView> relation = iterator.next();
	    	  if(relation.identifier().equals(identifier))
	    		  return relation;
	      }
	      //else
	      return null;
	}

	@Override
	public Collection<IRDFPredicateObject> objectSet() {
		//init set
		Collection<IRDFPredicateObject> objectSet = new LinkedList<IRDFPredicateObject>();
		//add relation instance
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_TYPE.resource(), RDFVocabulary.TYPE_BAG.resource()));
		//add relations as collection member
		int i = 1;
		for(IRelation<IFeatureView> relation : this.collection()){
			objectSet.add(new RDFPredicateObject(new RDFIdentifiableResource(RDF_NS + "#_" + i++), relation));
		}
		return objectSet;
	}

	@Override
	public Collection<? extends IRDFRepresentation> rdfCollection() {
		Collection<IRDFRepresentation> rdfCollection = new LinkedList<IRDFRepresentation>();
		for(IRelation<IFeatureView> relation : this.collection()){
			if(relation instanceof IRDFRepresentation)
				rdfCollection.add((IRDFRepresentation) relation);
		}
		return rdfCollection;
	}
	
	
	
}
