package de.tudresden.gis.fusion.data.geotools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.xml.Configuration;
import org.geotools.xml.PullParser;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.SAXException;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.metadata.data.IDescription;

public class GTFeatureCollection extends Resource implements IFeatureCollection {
	
	private Map<String,IFeature> features;
	private IDescription description;
	private ISpatialProperty spatialProperty;
	
	public GTFeatureCollection(IIRI iri, List<SimpleFeature> featureList, IDescription description) {
		super(iri);
		this.description = description;
		features = new HashMap<String,IFeature>();
		for(SimpleFeature feature : featureList){
			IIRI featureIRI = getCollectionId() == null ? new IRI(feature.getID()) : new IRI(getCollectionId() + "#" + feature.getID());
			features.put(featureIRI.asString(), new GTFeature(featureIRI, feature));
		}
	}
	
	public GTFeatureCollection(IIRI iri, List<SimpleFeature> featureList){
		this(iri, featureList, null);
	}
	
	public GTFeatureCollection(IIRI iri, SimpleFeatureCollection fc, IDescription description){
		this(iri, DataUtilities.list(fc), description);
	}
	
	public GTFeatureCollection(IIRI iri, SimpleFeatureCollection features){
		this(iri, features, null);
	}

	public GTFeatureCollection(IIRI iri, InputStream xmlIS, Configuration configuration) throws IOException {
		super(iri);
		features = new HashMap<String,IFeature>();
		PullParser gmlParser = new PullParser(configuration, xmlIS, SimpleFeature.class);
		SimpleFeature feature = null;
        try {
	        while((feature = (SimpleFeature) gmlParser.parse()) != null) {        	
	        	IIRI featureIRI = iri == null ? new IRI(feature.getID()) : new IRI(iri.asString() + "#" + feature.getID());
				features.put(featureIRI.asString(), new GTFeature(featureIRI, feature));
	        }
        } catch (SAXException se){
        	throw new IOException("Error in parsing GML input stream: " + se.getMessage());
        } catch (XMLStreamException xmle){
        	throw new IOException("Error in parsing GML input stream: " + xmle.getMessage());
        }
	}
	
	public String getCollectionId(){
		return this.getIdentifier().asString();
	}
	
	private void setSpatialProperty(SimpleFeatureCollection fc){
		this.spatialProperty = new GTSpatialProperty(fc.getSchema().getGeometryDescriptor(), fc.getID(), fc.getBounds());
	}
	
	public SimpleFeatureCollection getSimpleFeatureCollection(){
		return DataUtilities.collection(getFeatureCollectionList());
	}

	public List<SimpleFeature> getFeatureCollectionList(){
		List<SimpleFeature> fList = new ArrayList<SimpleFeature>();
		for(IFeature feature : this.getFeatures()){
			fList.add(((GTFeature) feature).getFeature());
		}
		return fList;
	}
	
	@Override
	public IFeature getFeatureById(String featureId){
		return features.get(featureId);
	}
	
	public boolean isResolvable(){
		return false;
	}

	@Override
	public IDescription getDescription() {
		return description;
	}
	
	public void setDescription(IDescription description) {
		this.description = description;
	}
	
	public int size(){
		return features.size();
	}
	
	@Override
	public Iterator<IFeature> iterator() {
		return features.values().iterator();
	}

	@Override
	public Collection<IFeature> getFeatures() {
		return features.values();
	}

	@Override
	public ISpatialProperty getSpatialProperty() {
		if(this.spatialProperty == null)
			setSpatialProperty(this.getSimpleFeatureCollection());
		return this.spatialProperty;
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
