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
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IResource;

public class GTFeatureCollection implements IIdentifiableResource,IFeatureCollection {
	
	private Map<String,IFeature> features;
	private IIRI iri;
	private IDataDescription description;
	private ISpatialProperty spatialProperty;
	
	public GTFeatureCollection(IIRI iri, List<SimpleFeature> featureList, IDataDescription description) {
		this.iri = iri;
		this.description = description;
		features = new HashMap<String,IFeature>();
		for(SimpleFeature feature : featureList){
			IIRI featureIRI = iri == null ? new IRI(feature.getID()) : new IRI(iri.asString() + "#" + feature.getID());
			features.put(featureIRI.asString(), new GTFeature(featureIRI, feature));
		}
	}
	
	public GTFeatureCollection(IIRI iri, List<SimpleFeature> featureList){
		this(iri, featureList, null);
	}
	
	public GTFeatureCollection(IIRI iri, SimpleFeatureCollection fc, IDataDescription description){
		this(iri, DataUtilities.list(fc), description);
	}
	
	public GTFeatureCollection(IIRI iri, SimpleFeatureCollection features){
		this(iri, features, null);
	}

	public GTFeatureCollection(IIRI iri, InputStream xmlIS, Configuration configuration) throws IOException {
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
        this.iri = iri;
	}
	
	private void setSpatialProperty(SimpleFeatureCollection fc){
		this.spatialProperty = new GTSpatialProperty(fc.getSchema().getGeometryDescriptor(), fc.getID(), fc.getBounds());
	}
	
	public SimpleFeatureCollection getSimpleFeatureCollection(){
		List<SimpleFeature> fList = new ArrayList<SimpleFeature>();
		for(IFeature feature : this.getFeatures()){
			fList.add(((GTFeature) feature).getFeature());
		}
		return DataUtilities.collection(fList);
	}

	@Override
	public IFeature getFeatureById(IIRI identifier){
		return features.get(identifier.asString());
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public boolean isBlank() {
		return iri == null;
	}
	
	public boolean isResolvable(){
		return false;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}
	
	public void setDescription(IDataDescription description) {
		this.description = description;
	}
	
	public int size(){
		return features.size();
	}

	@Override
	public Map<IIdentifiableResource, INode> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
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

}
