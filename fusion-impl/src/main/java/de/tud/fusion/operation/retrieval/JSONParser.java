package de.tud.fusion.operation.retrieval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import de.tud.fusion.data.feature.geotools.GTFeatureCollection;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.operation.AbstractOperation;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.InputConnector;
import de.tud.fusion.operation.description.OutputConnector;

public class JSONParser extends AbstractOperation {
	
	public final static String PROCESS_ID = JSONParser.class.getSimpleName();
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private Set<IInputConnector> inputConnectors;
	private Set<IOutputConnector> outputConnectors;

	/**
	 * constructor
	 */
	public JSONParser() {
		super(PROCESS_ID);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		//get data
		URL resourceURL;
		try {
			resourceURL = ((URILiteral) resourceConnector.getData()).resolve().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("JSON source is no valid URL: ", e);
		}
		//parse features
		GTFeatureCollection features;
		try {
			features = parseJSON(resourceURL);
		} catch (IOException e) {
			throw new RuntimeException("Could not parse JSON source", e);
		}
		//set output connector
		setOutputConnector(OUT_FEATURES, features);
	}

	/**
	 * parse JSON from URL
	 * @param resourceURL input URL
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSON(URL resourceURL) throws IOException {
		//parse HTTP connection
		if(resourceURL.getProtocol().toLowerCase().startsWith("http"))
			return parseJSONFromHTTP(resourceURL);		
		//parse file
		else if(resourceURL.getProtocol().toLowerCase().startsWith("file"))
			return parseJSONFromFile(resourceURL);
		//else: throw IOException
		else
			throw new IOException("Unsupported JSON source: " + resourceURL.toString());
	}
	
	/**
	 * parse JSON from HTTP
	 * @param resourceURL input URL (set as identifier)
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSONFromHTTP(URL resourceURL) throws IOException {
		//get connection
		HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
		urlConnection.connect();
		return parseJSON(resourceURL, urlConnection.getInputStream());
	}
	
	/**
	 * parse JSON from file
	 * @param resourceURL input URL (set as identifier)
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSONFromFile(URL resourceURL) throws IOException {
		File file = new File(resourceURL.getFile());
		if(!file.exists() || file.isDirectory())
			return null;
		//redirect based on content type
		return parseJSON(resourceURL, new FileInputStream(file));
	}
	
	/**
	 * parse JSON feature collection using GeoTools
	 * @param resourceURL input URL (set as identifier)
	 * @param stream input JSON stream
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSON(URL resourceURL, InputStream stream) throws IOException {
		FeatureJSON io = new FeatureJSON();
		DefaultFeatureCollection collection = (DefaultFeatureCollection) io.readFeatureCollection(stream);
		return new GTFeatureCollection(resourceURL.toString(), collection, null);
	}
	
	@Override
	public Set<IInputConnector> getInputConnectors() {
		if(inputConnectors != null)
			return inputConnectors;
		//generate descriptions
		inputConnectors = new HashSet<IInputConnector>();
		inputConnectors.add(new InputConnector(
				IN_RESOURCE, IN_RESOURCE, "Link to input GeoJSON",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class),
						new MandatoryConstraint()},
				null,
				null));	
		//return
		return inputConnectors;
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		if(outputConnectors != null)
			return outputConnectors;
		//generate descriptions
		outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.add(new OutputConnector(
				OUT_FEATURES, OUT_FEATURES, "Output feature collection",
				new IDataConstraint[]{
						new BindingConstraint(GTFeatureCollection.class),
						new MandatoryConstraint()},
				null));		
		//return
		return outputConnectors;
	}

	@Override
	public String getProcessTitle() {
		return "GeoJSON Parser";
	}

	@Override
	public String getProcessAbstract() {
		return "Parser for GeoJSON format";
	}

}
