package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.INode;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.*;

/**
 * RDF subject implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Subject extends Resource implements ISubject {
	
	private Object subject;
	private IMetadataForData metadata;
	private HashMap<IResource,Set<INode>> objectSet;

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param subject RDF subject
	 * @param metadata object description
	 */
	public Subject(IIdentifier identifier, Object subject, IMetadataForData metadata){
		super(identifier);
		this.subject = subject;
		this.metadata = metadata;
		initObjectSet();
	}

    /**
     * initialize object set
     */
	private void initObjectSet(){
	    this.objectSet = new HashMap<>();
    }

	@Override
	public Object resolve() {
		return subject;
	}

    /**
     * set subject
     * @param subject subject
     */
	protected void setSubject(Object subject) {
		this.subject = subject;
	}

	@Override
	public IMetadataForData getMetadata() {
		return metadata;
	}

	@Override
	public Set<IResource> getPredicates() {
		return objectSet.keySet();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) {
		return objectSet.get(predicate);
	}
	
	/**
	 * get single object from object set
	 * @param predicate input predicate
	 * @return single object
	 * @throws IllegalArgumentException if multiple objects exist for predicate
	 */
	public INode getObject(IResource predicate) throws IllegalArgumentException {
		if(!objectSet.containsKey(predicate))
			return null;
		Set<INode> nodeSet = objectSet.get(predicate);
		if(nodeSet.size() != 1)
			throw new IllegalArgumentException("predicate is related to multiple objects");
		return nodeSet.iterator().next();
	}
	
	/**
	 * put single INode object to object map
	 * @param predicate input predicate
	 * @param object input object
	 */
	protected void put(IResource predicate, INode object){
		if(predicate == null || object == null)
			return;
		//add to collection, if predicate is already set
		if(objectSet.containsKey(predicate))
			objectSet.get(predicate).add(object);
		//create predicate with object
		else {
			Set<INode> set = new HashSet<>(Collections.singletonList(object));
			objectSet.put(predicate, set);
		}
	}
	
	/**
	 * put node set into object map
	 * @param predicate input predicate
	 * @param objectSet input node set
	 */
	protected void put(IResource predicate, Collection<? extends INode> objectSet) {
		if(objectSet == null || objectSet.isEmpty())
			return;
		for(INode object : objectSet){
			this.put(predicate, object);
		}
	}
	
	/**
	 * drop predicate and associated objects from object set
	 * @param predicate input predicate to be removed
	 */
	protected void remove(IResource predicate){
		objectSet.remove(predicate);
	}
	
	/**
	 * get total number of objects related to subject
	 * @return number of objects
	 */
	public int getNumberOfObjects(){
		int i = 0;
		for(Set<INode> set : objectSet.values()){
			i += set.size();
		}
		return i;
	}

}
