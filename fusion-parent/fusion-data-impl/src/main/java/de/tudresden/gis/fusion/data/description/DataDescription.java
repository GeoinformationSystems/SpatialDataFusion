package de.tudresden.gis.fusion.data.description;

import java.util.Collection;
import java.util.LinkedList;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.rdf.IRDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class DataDescription extends RDFResource implements IDataDescription,IRDFTripleSet {

	String title, abstrakt;
	IDataProvenance provenance;
	private transient Collection<IRDFPredicateObject> objectSet;
	
	public DataDescription(IRI identifier, String title, String abstrakt, IDataProvenance provenance){
		super(identifier);
		this.title = title;
		this.abstrakt = abstrakt;
		this.provenance = provenance;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String abstrakt() {
		return abstrakt;
	}

	@Override
	public IDataProvenance provenance() {
		return provenance;
	}

	@Override
	public IRDFResource subject() {
		return this;
	}

	@Override
	public Collection<IRDFPredicateObject> objectSet() {
		if(objectSet != null)
			return objectSet;
		
		Collection<IRDFPredicateObject> objectSet = new LinkedList<IRDFPredicateObject>();
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_TITLE.resource(), new StringLiteral(title)));
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_ABSTRACT.resource(), new StringLiteral(abstrakt)));
		return objectSet;
	}

}
