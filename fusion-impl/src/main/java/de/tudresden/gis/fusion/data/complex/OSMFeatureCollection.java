package de.tudresden.gis.fusion.data.complex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.opengis.geometry.BoundingBox;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IDescription;
import de.tudresden.gis.fusion.misc.OSMCollection;

public class OSMFeatureCollection extends Resource implements IComplexData {

	private OSMCollection osmCollection;
	
	public OSMFeatureCollection(IIRI iri) throws MalformedURLException, XMLStreamException, IOException {
		super(iri);
		osmCollection = new OSMCollection(iri.asURL());
	}
	
	public OSMFeatureCollection(File file) throws FileNotFoundException, XMLStreamException{
		super(new IRI(file.toURI()));
		osmCollection = new OSMCollection(file);
	}
	
	public OSMFeatureCollection(String type, Map<String,String> tags, BoundingBox bbox) throws MalformedURLException, XMLStreamException, IOException {
		super(new IRI(DataUtilities.getOSMOverpassResource(type, tags, bbox)));
		osmCollection = new OSMCollection(super.getIdentifier().asURL());
	}
	
	public boolean isResolvable(){
		return false;
	}

	@Override
	public IDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public OSMCollection getOSMCollection(){
		return osmCollection;
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}