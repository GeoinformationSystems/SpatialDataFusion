package de.tudresden.gis.fusion.data.rdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RDFTripleSet implements IRDFTripleSet {

	IIRI identifier;
	private IResource subject;
	private Map<IIdentifiableResource,Set<INode>> objectSet;
	
	public RDFTripleSet(IIRI identifier, IResource subject, Map<IIdentifiableResource,Set<INode>> objectSet){
		this.identifier = identifier;
		this.subject = subject;
		this.objectSet = objectSet;
	}
	
	public RDFTripleSet(IResource subject, Map<IIdentifiableResource,Set<INode>> objectSet){
		this(null, subject, objectSet);
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
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		return objectSet;
	}
	
	/**
	 * add predicate - object to triple set
	 * @param predicate predicate
	 * @param object object
	 */
	public void addObject(IIdentifiableResource predicate, Set<INode> objects){
		if(objectSet == null)
			objectSet = new HashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(predicate, objects);
	}
	
	public Set<INode> getObject(IIdentifiableResource predicate){
		if(objectSet == null)
			return null;
		return objectSet.get(predicate);
	}

}
