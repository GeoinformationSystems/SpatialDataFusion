package de.tudresden.gis.fusion.operation.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.OSMCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IParser;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class OSMXMLParser extends AOperationInstance implements IParser {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	private final String OUT_NODES = "OUT_NODES";
	private final String OUT_WAYS = "OUT_WAYS";
	
	private String resource;
	private boolean bWithIndex;
	
	@Override
	public void execute() {
		
		URILiteral osmResource = (URILiteral) input(IN_RESOURCE);
		resource = osmResource.resolve().toString();
		bWithIndex = inputContainsKey(IN_WITH_INDEX) ? ((BooleanLiteral) input(IN_WITH_INDEX)).resolve() : false;
		
		GTFeatureCollection[] osmFC;

		URL url;
		try {
			url = osmResource.resolve().toURL();
		} catch (MalformedURLException e) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "OSM source is no valid URL");
		}
		//parse HTTP connection
		if(url.getProtocol().toLowerCase().startsWith("http"))
			osmFC = parseOSMFromHTTP(url);		
		//parse file
		else if(url.getProtocol().toLowerCase().startsWith("file"))
			osmFC = parseOSMFromFile(url);
		else
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Unsupported OSM source");
        
		setOutput(OUT_NODES, osmFC[0]);
		setOutput(OUT_WAYS, osmFC[1]);
	}
	
	private GTFeatureCollection[] parseOSMFromHTTP(URL url) {
		try {
			//get connection
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			return parse(urlConnection.getInputStream());						
		} catch (Exception e){
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not parse OSM XML", e);
		}
	}
	
	private GTFeatureCollection[] parseOSMFromFile(URL url) {
		File file = new File(url.getFile());
		if(!file.exists() || file.isDirectory())
			return null;
		//parse
	    try {
			return parse(new FileInputStream(file));
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not parse OSM XML", e);
		}
	}

	private GTFeatureCollection[] parse(InputStream stream) throws XMLStreamException, SAXException {
		//parse OSM collection object
		OSMCollection osmCollection = new OSMCollection(stream);
		//get feature collections for nodes and ways
		GTFeatureCollection[] osmFC = new GTFeatureCollection[2];
		if(bWithIndex){
			osmFC[0] = new GTIndexedFeatureCollection(resource, osmCollection.getNodes());
			osmFC[1] = new GTIndexedFeatureCollection(resource, osmCollection.getWays());
		}
        else {
        	osmFC[0] = new GTFeatureCollection(resource, osmCollection.getNodes());
			osmFC[1] = new GTFeatureCollection(resource, osmCollection.getWays());
        }
		return osmFC;
	}

	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "OSM XML parser";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Parser for OSM XML format";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
