package de.tud.fusion.data.feature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tud.fusion.data.IDataCollection;
import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.data.rdf.Subject;

/**
 * GeoTools feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureCollection<T extends AbstractFeature> extends Subject implements IDataCollection<T> {
	
	private final IResource PREDICATE_TYPE = RDFVocabulary.TYPE.getResource();
	private final IResource PREDICATE_MEMBER = RDFVocabulary.MEMBER.getResource();
	
	private final IResource TYPE_BAG = RDFVocabulary.BAG.getResource();
	
	/**
	 * map of features
	 */
	private transient Map<String,T> featureMap;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection Feature collection
	 * @param description collection description
	 */
	public FeatureCollection(String identifier, Collection<T> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
		initSubject(featureCollection);
	}

	/**
	 * initialize feature subject
	 * @param identifier 
	 * @param featureCollection
	 */
	private void initSubject(Collection<T> featureCollection) {
		//set resource type
		put(PREDICATE_TYPE, TYPE_BAG);
		for(AbstractFeature feature : resolve()){
			put(PREDICATE_MEMBER, feature);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> resolve(){
		return (Collection<T>) super.resolve();
	}
	
	@Override
	public Iterator<T> iterator() {
		return this.resolve().iterator();
	}

	/**
	 * get size of collection
	 * @return number of features in collection
	 */
	public int size() {
		return resolve().size();
	}

	/**
	 * get feature by id
	 * @param identifier feature id
	 * @return feature with specified id or null, if no such feature exists
	 */
	public T getFeatureById(String identifier) {
		if(featureMap == null)
			initFeatureMap();
		return featureMap.get(identifier);
	}
	
	/**
	 * check if collection contains specified feature
	 * @param identifier feature identifier
	 * @return true, if collection contains feature with specified id
	 */
	public boolean containsId(String identifier) {
		return featureMap.containsKey(identifier);
	}

	/**
	 * add a feature
	 * @param feature input feature
	 */
	public void add(T feature) {
		this.resolve().add(feature);
		if(featureMap == null)
			initFeatureMap();
		else {
			featureMap.put(feature.getIdentifier(), feature);
			put(PREDICATE_MEMBER, feature);
		}
	}
	
	/**
	 * initialize feature map
	 */
	private void initFeatureMap() {
		featureMap = new HashMap<String,T>();
		for(T feature : resolve()){
			featureMap.put(feature.getIdentifier(), feature);
		}
	}

}
