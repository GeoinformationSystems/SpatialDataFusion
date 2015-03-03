package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotools.xml.Configuration;

import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;

public class GMLParser extends AOperation implements IDataRetrieval {

	public static final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	public static final String OUT_FEATURES = "OUT_FEATURES";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};
	
	@Override
	public void execute() throws ProcessException {
		
		//get input url
		URILiteral gmlResource = (URILiteral) getInput(IN_RESOURCE);		
		BooleanLiteral inWithIndex = (BooleanLiteral) getInput(IN_WITH_INDEX);
				
		boolean bWithIndex = inWithIndex == null ? false : inWithIndex.getValue();
		IIRI identifier = new IRI(gmlResource.getIdentifier());

		GTFeatureCollection gmlFC = null;
		
		//parse HTTP connection
		if(gmlResource.getProtocol().toLowerCase().startsWith("http"))
			gmlFC = parseGMLFromHTTP(identifier, gmlResource, bWithIndex);		
		//parse file
		else if(gmlResource.getProtocol().toLowerCase().startsWith("file"))
			gmlFC = parseGMLFromFile(identifier, gmlResource, bWithIndex);
		
		if(gmlFC == null)
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "An error occurred while parsing GML input");
			
		//set output
		setOutput(OUT_FEATURES, gmlFC);
		
	}

	private GTFeatureCollection parseGMLFromHTTP(IIRI identifier, URILiteral gmlResource, boolean bWithIndex) {
		try {
			//get connection
			HttpURLConnection urlConnection = (HttpURLConnection) new URL(gmlResource.getIdentifier()).openConnection();
			urlConnection.connect();
			return parseGML(urlConnection.getContentType(), identifier, urlConnection.getInputStream(), bWithIndex);						
		} catch (IOException e){
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
	}
	
	private GTFeatureCollection parseGMLFromFile(IIRI identifier, URILiteral gmlResource, boolean bWithIndex) {
		File file = new File(identifier.asURL().getFile());
		if(!file.exists() || file.isDirectory())
			return null;
		//redirect based on content type
		try {
			return parseGML(getContentType(file), identifier, new FileInputStream(file), bWithIndex);			
		} catch (IOException | XMLStreamException e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
	}
	
	private GTFeatureCollection parseGML(String contentType, IIRI identifier, InputStream stream, boolean bWithIndex) {		
		try {
			//brute force if content type is null
			if(contentType == null)
				return bruteParse(identifier, stream, bWithIndex);
			//redirect based on content type
			if(contentType.contains("3.2"))
				return parseGML32(identifier, stream, bWithIndex);
			else if(contentType.contains("3."))
				return parseGML3(identifier, stream, bWithIndex);
			else if(contentType.contains("2."))
				return parseGML2(identifier, stream, bWithIndex);
			else
				return null;
			
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
			
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private GTFeatureCollection bruteParse(IIRI identifier, InputStream stream, boolean bWithIndex) {
		GTFeatureCollection gmlFC = null;
		try {
			gmlFC = parseGML32(identifier, stream, bWithIndex);
			if(gmlFC == null || gmlFC.size() == 0)
				gmlFC = parseGML3(identifier, stream, bWithIndex);
			if(gmlFC == null || gmlFC.size() == 0)
				gmlFC = parseGML2(identifier, stream, bWithIndex);
			
		} catch (IOException e){ 
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
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

	private GTFeatureCollection parseGML32(IIRI identifier, InputStream stream, boolean bWithIndex) throws IOException {
		return parseGML(identifier, stream, new org.geotools.gml3.v3_2.GMLConfiguration(), bWithIndex);
	}

	private GTFeatureCollection parseGML3(IIRI identifier, InputStream stream, boolean bWithIndex) throws IOException {
		return parseGML(identifier, stream, new org.geotools.gml3.GMLConfiguration(), bWithIndex);
	}

	private GTFeatureCollection parseGML2(IIRI identifier, InputStream stream, boolean bWithIndex) throws IOException {
		return parseGML(identifier, stream, new org.geotools.gml2.GMLConfiguration(), bWithIndex);
	}
	
	private GTFeatureCollection parseGML(IIRI identifier, InputStream stream, Configuration configuration, boolean bWithIndex) throws IOException {
		if(bWithIndex)
			return new GTIndexedFeatureCollection(identifier, stream, configuration);
        else
        	return new GTFeatureCollection(identifier, stream, configuration);
	}

	@Override
	public void setFilter(IFilter filter) {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Parser for GML";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_RESOURCE, "GML resource",
				new IIORestriction[]{
					ERestrictions.BINDING_URIRESOURCE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_WITH_INDEX, "if set true, a spatial index is build",
				new BooleanLiteral(true),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			)
		};				
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_FEATURES, "Output features",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
				}
			)
		};
	}

	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}
	
}
