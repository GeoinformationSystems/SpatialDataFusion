package de.tudresden.gis.fusion.operation.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotools.xml.Configuration;
import org.xml.sax.SAXException;

import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IParser;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class GMLParser extends AOperationInstance implements IParser {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private String resource;
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	@Override
	public void execute() {
		
		URILiteral gmlResource = (URILiteral) getInput(IN_RESOURCE);
		resource = gmlResource.resolve().toString();
		boolean bWithIndex = ((BooleanLiteral) getInput(IN_WITH_INDEX)).resolve();
		
		GTFeatureCollection gmlFC = null;

		URL url;
		try {
			url = gmlResource.resolve().toURL();
		} catch (MalformedURLException e) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "GML source is no valid URL");
		}
		//parse HTTP connection
		if(url.getProtocol().toLowerCase().startsWith("http"))
			gmlFC = parseGMLFromHTTP(url, bWithIndex);		
		//parse file
		else if(url.getProtocol().toLowerCase().startsWith("file"))
			gmlFC = parseGMLFromFile(url, bWithIndex);
		else
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Unsupported GML source");
        
		setOutput(OUT_FEATURES, gmlFC);
	}
	
	private GTFeatureCollection parseGMLFromHTTP(URL url, boolean bWithIndex) {
		try {
			//get connection
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			return parseGML(getContentTypeFromURL(urlConnection, url.toString().toLowerCase()), urlConnection.getInputStream(), bWithIndex);						
		} catch (Exception e){
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not parse GML", e);
		}
	}
	
	private String getContentTypeFromURL(HttpURLConnection urlConnection, String sUrl) {
		if(urlConnection.getContentType() != null && urlConnection.getContentType().toLowerCase().contains("gml"))
			return urlConnection.getContentType();
		else if(sUrl.contains("wfs")){
			if(sUrl.contains("version=1.0.0"))	
				return "2.0";
			else if(sUrl.contains("version=1.1.0"))	
				return "3.1.1";
			else if(sUrl.contains("version=2.0.0"))
				return "3.2";
		}
		return null;
	}

	private GTFeatureCollection parseGMLFromFile(URL url, boolean bWithIndex) {
		File file = new File(url.getFile());
		if(!file.exists() || file.isDirectory())
			return null;
		//redirect based on content type
		try {
			return parseGML(getContentType(file), new FileInputStream(file), bWithIndex);			
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not parse GML", e);
		}
	}
	
	private GTFeatureCollection parseGML(String contentType, InputStream stream, boolean bWithIndex) throws IOException, XMLStreamException, SAXException {		
		//test different encodings if content type is null
		if(contentType == null)
			return parse(stream, bWithIndex);
		//redirect based on content type
		if(contentType.contains("3.2"))
			return parseGML32(stream, bWithIndex);
		else if(contentType.contains("3."))
			return parseGML3(stream, bWithIndex);
		else if(contentType.contains("2."))
			return parseGML2(stream, bWithIndex);
		else if(contentType.contains("text/xml"))
			return parse(stream, bWithIndex);
		else
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not determine GML version");
	}

	private GTFeatureCollection parse(InputStream stream, boolean bWithIndex) {
		GTFeatureCollection gmlFC = null;
		try {
			gmlFC = parseGML32(stream, bWithIndex);
			if(gmlFC == null || gmlFC.size() == 0)
				gmlFC = parseGML3(stream, bWithIndex);
			if(gmlFC == null || gmlFC.size() == 0)
				gmlFC = parseGML2(stream, bWithIndex);
			
		} catch (Exception e){ 
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not parse GML", e);
		}
		
		return gmlFC;
	}

	private String getContentType(File file) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(file));
		String contentType = null;
		while(reader.hasNext()){
			int event = reader.next();
			if(event == XMLStreamConstants.START_ELEMENT){
				//get content type from xml namespace
				for(int i=0; i<reader.getNamespaceCount(); i++) {
			        String nsURI = reader.getNamespaceURI(i);
			        contentType = getGMLContentTypeFromString(nsURI);
			        if(contentType != null)
			        	break;
				}
				//get content type from xsi:schemaLocation
				String schemaLocation = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
				contentType = getGMLContentTypeFromString(schemaLocation);
				break;
			}
		}
		reader.close();
		return contentType;
	}
	
	private String getGMLContentTypeFromString(String input){
		if(input.matches("(.*gml/3\\.2.*)|(.*wfs/2\\.0.*)")){
        	return "gml/3.2.1";
        }
        else if(input.matches("(.*gml/3\\.2.*)|(.*wfs/1\\.1\\.0.*)")){
        	return "gml/3.1.1";
		}
        else if(input.matches("(.*gml/2.*)|(.*wfs/1\\.0\\.0.*)")){
        	return "gml/2.1.2";
		}
		return null;
	}

	private GTFeatureCollection parseGML32(InputStream stream, boolean bWithIndex) throws IOException, XMLStreamException, SAXException {
		return parseGML(stream, new org.geotools.gml3.v3_2.GMLConfiguration(), bWithIndex);
	}

	private GTFeatureCollection parseGML3(InputStream stream, boolean bWithIndex) throws IOException, XMLStreamException, SAXException {
		return parseGML(stream, new org.geotools.gml3.GMLConfiguration(), bWithIndex);
	}

	private GTFeatureCollection parseGML2(InputStream stream, boolean bWithIndex) throws IOException, XMLStreamException, SAXException {
		return parseGML(stream, new org.geotools.gml2.GMLConfiguration(), bWithIndex);
	}
	
	private GTFeatureCollection parseGML(InputStream stream, Configuration configuration, boolean bWithIndex) throws XMLStreamException, IOException, SAXException {
		if(bWithIndex)
        	return new GTIndexedFeatureCollection(resource, stream, configuration);
        else
        	return new GTFeatureCollection(resource, stream, configuration);
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "GML parser";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Parser for OGC GML format";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		if(inputDescriptions == null){
			inputDescriptions = new HashSet<IInputDescription>();
			inputDescriptions.add(new InputDescription(IN_RESOURCE, IN_RESOURCE, "Link to input GML)",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_RESOURCE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{URILiteral.class})
					}));
			inputDescriptions.add(new InputDescription(IN_WITH_INDEX, IN_WITH_INDEX, "If true, an indexed feature collection is created",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
					},
					new BooleanLiteral(false)));
		}
		return inputDescriptions;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		if(outputDescriptions == null){
			outputDescriptions = new HashSet<IOutputDescription>();
			outputDescriptions.add(new OutputDescription(
					OUT_FEATURES, OUT_FEATURES, "Output feature collection",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_FEATURES),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
		}
		return outputDescriptions;
	}

}
