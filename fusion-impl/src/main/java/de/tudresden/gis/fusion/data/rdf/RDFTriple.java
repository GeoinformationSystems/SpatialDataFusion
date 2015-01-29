package de.tudresden.gis.fusion.data.rdf;

public class RDFTriple implements IRDFTriple {
	
	IIRI identifier;
	private IResource subject;
	private IIdentifiableResource predicate;
	private INode object;
	
	public RDFTriple(IIRI identifier, IResource subject, IIdentifiableResource predicate, INode object){
		this.identifier = identifier;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	public RDFTriple(IResource subject, IIdentifiableResource predicate, INode object){
		this(null, subject, predicate, object);
	}

	@Override
	public IResource getSubject() {
		return subject;
	}

	@Override
	public Object getIdentifier() {
		return identifier;
	}

	@Override
	public IIdentifiableResource getPredicate() {
		return predicate;
	}

	@Override
	public INode getObject() {
		return object;
	}

}
