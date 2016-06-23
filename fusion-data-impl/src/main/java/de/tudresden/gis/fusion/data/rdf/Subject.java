package de.tudresden.gis.fusion.data.rdf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * RDF subject implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Subject extends Resource implements ISubject {

	/**
	 * object set for the subject
	 */
	private HashMap<IResource,Set<INode>> objectSet;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public Subject(String identifier, boolean identifiable, HashMap<IResource,Set<INode>> objectSet){
		super(identifier, identifiable);
		initObjectSet(objectSet);
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public Subject(String identifier){
		this(identifier, false, null);
	}
	
	/**
	 * initialize object set
	 * @param objectSet
	 */
	private void initObjectSet(HashMap<IResource, Set<INode>> objectSet) {
		if(objectSet == null)
			this.objectSet = new HashMap<IResource,Set<INode>>();
		else
			this.objectSet = objectSet;
	}

	@Override
	public Set<IResource> getPredicates() {
		return objectSet.keySet();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) throws IllegalArgumentException {
		return objectSet.get(predicate);
	}
	
	/**
	 * put single INode object to object map
	 * @param predicate input predicate
	 * @param object input object
	 * @param mandatory if true, the object must not be null, otherwise an IllegalArgumentException is thrown
	 */
	public void put(IResource predicate, INode object, boolean mandatory){
		//check object
		if(object == null){
			if(mandatory)
				throw new IllegalArgumentException("Object must not be null");
			else
				return;				
		}
		//add to collection, if predicate is already set
		if(objectSet.containsKey(predicate))
			objectSet.get(predicate).add(object);
		//create object collection
		else {
			Set<INode> set = new HashSet<INode>(Arrays.asList(object));
			set.add(object);
			objectSet.put(predicate, set);
		}
	}
	
	/**
	 * put single node to object map
	 * @param predicate input predicate
	 * @param object input object
	 */
	public void put(IResource predicate, INode object){
		put(predicate, object, false);
	}
	
	/**
	 * put node set into object map
	 * @param predicate input predicate
	 * @param objectSet input node set
	 * @param mandatory if true, the object set must not be null or empty, otherwise an IllegalArgumentException is thrown
	 */
	public void put(IResource predicate, Collection<? extends INode> objectSet, boolean mandatory) {
		//check object
		if(objectSet == null || objectSet.isEmpty()){
			if(mandatory)
				throw new IllegalArgumentException("Object must not be null");
			else
				return;				
		}
		//put nodes into map
		for(INode object : objectSet){
			this.put(predicate, object, mandatory);
		}
	}
	
	/**
	 * put node set into object map
	 * @param predicate input predicate
	 * @param objectSet input node set
	 */
	public void put(IResource predicate, Collection<? extends INode> objectSet) {
		put(predicate, objectSet, false);
	}
	
	/**
	 * get single object from object set
	 * @param predicate input predicate
	 * @return single object
	 * @throws IllegalArgumentException if multiple objects exist for predicate
	 */
	public INode getSingle(IResource predicate){
		if(!objectSet.containsKey(predicate))
			return null;
		Set<INode> nodeSet = objectSet.get(predicate);
		if(nodeSet.size() != 1)
			throw new IllegalArgumentException("predicate is related to multiple objects");
		return nodeSet.iterator().next();
	}
	
	/**
	 * get total size of objects related to subject
	 */
	public int getNumberOfObjects(){
		int i = 0;
		for(Set<INode> set : objectSet.values()){
			i += set.size();
		}
		return i;
	}

}
