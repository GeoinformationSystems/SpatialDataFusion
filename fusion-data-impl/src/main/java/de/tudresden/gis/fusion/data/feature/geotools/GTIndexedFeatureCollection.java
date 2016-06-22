package de.tudresden.gis.fusion.data.feature.geotools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.STRtree;

import de.tudresden.gis.fusion.data.description.IDataDescription;

public class GTIndexedFeatureCollection extends GTFeatureCollection {

	private STRtree index;
	
	public GTIndexedFeatureCollection(String identifier, Collection<GTFeature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
		buildIndex();
	}
	
	public GTIndexedFeatureCollection(String identifier, Collection<GTFeature> featureCollection){
		this(identifier, featureCollection, null);
	}
	
	public GTIndexedFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection, IDataDescription description){
		this(identifier, getGTCollection(featureCollection), description);
	}
	
	public GTIndexedFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		this(identifier, getGTCollection(featureCollection), null);
	}
	
	public GTIndexedFeatureCollection(String identifier, InputStream xmlIS, Configuration configuration) throws IOException, XMLStreamException, SAXException {
		super(identifier, xmlIS, configuration);
		buildIndex();
	}
	
	public GTIndexedFeatureCollection(GTFeatureCollection featureCollection){
		this(featureCollection.identifier(), featureCollection.resolve(), featureCollection.getDescription());
	}
	
	/**
	 * build spatial index for feature collection
	 */
	private void buildIndex(){
		this.index = new STRtree();
		try (FeatureIterator<? extends Feature> iterator = super.collection().features() ){
		     while(iterator.hasNext()){
		          addFeatureToIndex(iterator.next());
		     }
		 }
	}
	
	/**
	 * adds feature to spatial index
	 * @param feature input feature
	 */
	private void addFeatureToIndex(Feature feature){
		index.insert(ReferencedEnvelope.reference(feature.getBounds()), feature);
	}
	
	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @return all intersecting features
	 */
	@SuppressWarnings("unchecked")
	public List<Feature> boundsIntersect(Feature feature){
		return this.index.query(ReferencedEnvelope.reference(feature.getBounds()));
	}
	
	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @param buffer buffer tolerance applied to bounds
	 * @return all intersecting features within tolerance
	 */
	@SuppressWarnings("unchecked")
	public List<Feature> boundsIntersect(Feature feature, double buffer){
		Envelope envelope = ReferencedEnvelope.reference(feature.getBounds());
		envelope.expandBy(buffer);
		return this.index.query(envelope);
	}

	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @param buffer buffer tolerance applied to bounds
	 * @return all intersecting features within tolerance
	 */
	public List<GTFeature> boundsIntersect(GTFeature feature, double buffer) {
		List<GTFeature> list = new ArrayList<GTFeature>();
		List<Feature> intersections = boundsIntersect(feature.resolve(), buffer);
		for(Feature feat : intersections){
			list.add(this.elementById(feat.getIdentifier().toString()));
		}
		return list;
	}
}
