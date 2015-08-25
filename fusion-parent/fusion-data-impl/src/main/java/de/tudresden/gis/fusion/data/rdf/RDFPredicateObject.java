package de.tudresden.gis.fusion.data.rdf;

public class RDFPredicateObject implements IRDFPredicateObject {
	
	private IRDFIdentifiableResource predicate;
	private IRDFNode object;
	
	public RDFPredicateObject(IRDFIdentifiableResource predicate, IRDFNode object){
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public IRDFIdentifiableResource predicate() {
		return predicate;
	}

	@Override
	public IRDFNode object() {
		return object;
	}

}
