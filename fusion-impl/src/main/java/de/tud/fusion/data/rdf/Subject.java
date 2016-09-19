package de.tud.fusion.data.rdf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * RDF subject implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Subject extends ResourceData implements ISubject {
	
	/**
	 * object set
	 */
	HashMap<IResource,Set<INode>> objectSet;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param objectSet predicate-object set
	 */
	public Subject(String identifier, Object object, IDataDescription description){
		super(identifier, object, description);
		objectSet = new HashMap<IResource,Set<INode>>();
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
	public INode getObject(IResource predicate){
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
		//add to collection, if predicate is already set
		if(objectSet.containsKey(predicate))
			objectSet.get(predicate).add(object);
		//create object
		else {
			Set<INode> set = new HashSet<INode>(Arrays.asList(object));
			set.add(object);
			objectSet.put(predicate, set);
		}
	}
	
	/**
	 * put node set into object map
	 * @param predicate input predicate
	 * @param objectSet input node set
	 */
	protected void put(IResource predicate, Collection<? extends INode> objectSet) {
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
