package de.tudresden.gis.fusion.data.rdf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjectSet extends HashMap<IIdentifiableResource,Set<INode>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * put single INode object to Map
	 * @param predicate input predicate
	 * @param object input object
	 */
	public void put(IIdentifiableResource predicate, INode object){
		put(predicate, object, false);
	}
	
	/**
	 * put single INode object to Map
	 * @param predicate input predicate
	 * @param object input object
	 * @param mandatory specify mandatory object; if true, an IllegalArgumentException is thrown if object is null
	 */
	public void put(IIdentifiableResource predicate, INode object, boolean mandatory){
		//check object
		if(object == null){
			if(mandatory)
				throw new IllegalArgumentException("Object must not be null");
			else
				return;				
		}
		//add to collection, if predicate is already set
		if(this.containsKey(predicate))
			this.get(predicate).add(object);
		//create object collection
		else {
			Set<INode> set = new HashSet<INode>(Arrays.asList(object));
			set.add(object);
			this.put(predicate, set);
		}
	}
	
	/**
	 * put node set into Map
	 * @param predicate input predicate
	 * @param objectSet input node set
	 */
	public void put(IIdentifiableResource predicate, Collection<? extends INode> objectSet) {
		put(predicate, objectSet, false);
	}
	
	/**
	 * put node set into Map
	 * @param predicate input predicate
	 * @param objectSet input node set
	 * @param mandatory specify mandatory objectSet; if true, an IllegalArgumentException is thrown if objectSet is null
	 */
	public void put(IIdentifiableResource predicate, Collection<? extends INode> objectSet, boolean mandatory) {
		//check object
		if(objectSet == null){
			if(mandatory)
				throw new IllegalArgumentException("Object must not be null");
			else
				return;				
		}
		//put nodes into map
		for(INode object : objectSet){
			put(predicate, object);
		}
	}
	
	/**
	 * get object collection
	 * @param predicate input predicate
	 * @param clazz collection type generic
	 * @return object collection
	 */
	@SuppressWarnings("unchecked")
	public <T extends INode> Collection<T> get(IIdentifiableResource predicate, Class<T> clazz){
		Set<INode> objectSet = this.get(predicate);
		Collection<T> collection = new HashSet<T>();
		for(INode node : objectSet){
			if(clazz.isAssignableFrom(node.getClass()))
				collection.add((T) node);
		}
		return collection;
	}
	
	/**
	 * get first element in Set (for single element Sets)
	 * @param predicate input predicate
	 * @return first object in Set
	 */
	public INode getFirst(IIdentifiableResource predicate){
		if(!super.containsKey(predicate))
			return null;
		Iterator<INode> iterator = super.get(predicate).iterator();
		if(iterator.hasNext())
			return super.get(predicate).iterator().next();
		else
			return null;
	}
	
	/**
	 * get total size of object set (total number of objects)
	 */
	public int numberOfObjects(){
		int i = 0;
		for(Set<INode> set : this.values()){
			i += set.size();
		}
		return i;
	}

}
