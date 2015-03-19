package de.tudresden.gis.fusion.data.geotools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.xml.Configuration;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.STRtree;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.metadata.data.IDescription;

public class GTIndexedFeatureCollection extends GTFeatureCollection {
	
	//index tree
	protected STRtree index;

	public GTIndexedFeatureCollection(IIRI iri, List<SimpleFeature> featureList, IDescription description) {
		super(iri, featureList, description);
		buildIndex();
	}
	
	public GTIndexedFeatureCollection(IIRI iri, List<SimpleFeature> featureList){
		this(iri, featureList, null);
	}
	
	public GTIndexedFeatureCollection(IIRI iri, SimpleFeatureCollection fc, IDescription description){
		this(iri, DataUtilities.list(fc), description);
	}
	
	public GTIndexedFeatureCollection(IIRI iri, SimpleFeatureCollection features){
		this(iri, features, null);
	}

	public GTIndexedFeatureCollection(IIRI iri, InputStream xmlIS, Configuration configuration) throws IOException {
		super(iri, xmlIS, configuration);
		buildIndex();
	}
	
	public GTIndexedFeatureCollection(GTFeatureCollection collection) throws IOException {
		super(collection.getIdentifier(), collection.getFeatureCollectionList(), collection.getDescription());
		buildIndex();
	}
	
	private void buildIndex(){
		this.index = new STRtree();
		Iterator<IFeature> iter = this.iterator();
		while(iter.hasNext()){
			addFeatureToIndex(iter.next());
		}
	}
	
	private void addFeatureToIndex(IFeature feature){
		double[] bounds = feature.getDefaultSpatialProperty().getBounds();
		this.index.insert(new Envelope(bounds[0], bounds[2], bounds[1], bounds[3]), feature);
	}

	@SuppressWarnings("unchecked")
	public List<IFeature> boundsIntersect(IFeature feature){
		double[] bounds = feature.getDefaultSpatialProperty().getBounds();
		Envelope envelope = new Envelope(bounds[0], bounds[2], bounds[1], bounds[3]);
		return this.index.query(envelope);
	}
	
	@SuppressWarnings("unchecked")
	public List<IFeature> boundsIntersect(IFeature feature, double buffer){
		double[] bounds = feature.getDefaultSpatialProperty().getBounds();
		Envelope envelope = new Envelope(bounds[0], bounds[2], bounds[1], bounds[3]);
		envelope.expandBy(buffer);
		return this.index.query(envelope);
	}
}
