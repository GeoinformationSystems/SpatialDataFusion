package de.tudresden.gis.fusion.data.complex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.opengis.geometry.BoundingBox;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.misc.OSMCollection;

public class OSMFeatureCollection implements IResource,IComplexData {

	private OSMCollection osmCollection;
	private IIRI iri;
	
	public OSMFeatureCollection(IIRI iri) throws MalformedURLException, XMLStreamException, IOException {
		osmCollection = new OSMCollection(iri.asURI().toURL());
		this.iri = iri;
	}
	
	public OSMFeatureCollection(File file) throws FileNotFoundException, XMLStreamException{
		osmCollection = new OSMCollection(file);
		iri = new IRI(file.toURI());
	}
	
	public OSMFeatureCollection(String type, Map<String,String> tags, BoundingBox bbox) throws MalformedURLException, XMLStreamException, IOException {
		iri = new IRI(DataUtilities.getOSMOverpassResource(type, tags, bbox));
		osmCollection = new OSMCollection(iri.asURI().toURL());
	}
	
	public boolean isResolvable(){
		return false;
	}

	@Override
	public IDataDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBlank() {
		return iri == null;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}
	
	public OSMCollection getOSMCollection(){
		return osmCollection;
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

}