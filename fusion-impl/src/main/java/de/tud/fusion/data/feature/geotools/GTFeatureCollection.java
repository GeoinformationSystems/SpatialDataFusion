package de.tud.fusion.data.feature.geotools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import de.tud.fusion.data.feature.AbstractFeature;
import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.feature.FeatureCollection;

/**
 * GeoTools feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeatureCollection extends FeatureCollection<GTFeature> {
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools GTFeature collection
	 * @param description collection description
	 */
	public GTFeatureCollection(String identifier, Collection<GTFeature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools feature collection
	 * @param description collection description
	 */
	public GTFeatureCollection(String identifier, SimpleFeatureCollection featureCollection, IDataDescription description){
		this(identifier, getGTCollection(identifier, featureCollection), description);
	}

	/**
	 * get GeoTools FeatureCollection
	 * @return GeoTools FeatureCollection
	 */
	public SimpleFeatureCollection collection() {
		List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
		for(AbstractFeature feature : resolve()){
			featureList.add((SimpleFeature) feature.resolve());
		}
		return DataUtilities.collection(featureList);
	}
	
	/**
	 * create collection from GeoTools feature collection
	 * @param featureCollection input collection
	 * @return collection of GTFeature implementations
	 */
	public static Collection<GTFeature> getGTCollection(String collectionId, SimpleFeatureCollection featureCollection) {
		Collection<GTFeature> collection = new HashSet<GTFeature>();
		try (SimpleFeatureIterator iterator = featureCollection.features()){
		     while(iterator.hasNext()){
		    	 SimpleFeature feature = iterator.next();
		    	 String featureID = collectionId == null ? feature.getIdentifier().getID() : (collectionId + "#" + feature.getIdentifier().getID());
		    	 collection.add(new GTFeature(featureID, feature, null, null));
		     }
		}
		return collection;
	}

}
