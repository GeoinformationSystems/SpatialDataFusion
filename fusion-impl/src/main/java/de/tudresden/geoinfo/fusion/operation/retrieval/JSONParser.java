package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JSONParser extends AbstractOperation {

	private static final IIdentifier PROCESS = new Identifier(JSONParser.class.getSimpleName());

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
	private final static IIdentifier IN_WITH_INDEX = new Identifier("IN_WITH_INDEX");

	private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");

	/**
	 * constructor
	 */
	public JSONParser() {
		super(PROCESS);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		IInputConnector indexConnector = getInputConnector(IN_WITH_INDEX);
		//get data
		URI resourceURI = ((URILiteral) resourceConnector.getData()).resolve();
		boolean withIndex = ((BooleanLiteral) indexConnector.getData()).resolve();
		//parse features
		GTFeatureCollection features;
		try {
			features = parseJSON(resourceURI.toURL(), withIndex);
		} catch (IOException e) {
			throw new RuntimeException("Could not parse JSON source", e);
		}
		//set output connector
		connectOutput(OUT_FEATURES, features);
	}

	/**
	 * parse JSON from URL
	 * @param resourceURL input URL
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSON(URL resourceURL, boolean withIndex) throws IOException {
		//parse HTTP connection
		if(resourceURL.getProtocol().toLowerCase().startsWith("http"))
			return parseJSONFromHTTP(resourceURL, withIndex);
		//parse file
		else if(resourceURL.getProtocol().toLowerCase().startsWith("file"))
			return parseJSONFromFile(resourceURL, withIndex);
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
	private GTFeatureCollection parseJSONFromHTTP(URL resourceURL, boolean withIndex) throws IOException {
		//get connection
		HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
		urlConnection.connect();
		return parseJSON(resourceURL, urlConnection.getInputStream(), withIndex);
	}
	
	/**
	 * parse JSON from file
	 * @param resourceURL input URL (set as identifier)
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSONFromFile(URL resourceURL, boolean withIndex) throws IOException {
		File file = new File(resourceURL.getFile());
		if(!file.exists() || file.isDirectory())
			return null;
		//redirect based on content type
		return parseJSON(resourceURL, new FileInputStream(file), withIndex);
	}
	
	/**
	 * parse JSON feature collection using GeoTools
	 * @param resourceURL input URL (set as identifier)
	 * @param stream input JSON stream
	 * @return GeoTools feature collection
	 * @throws IOException
	 */
	private GTFeatureCollection parseJSON(URL resourceURL, InputStream stream, boolean withIndex) throws IOException {
		FeatureJSON io = new FeatureJSON();
		DefaultFeatureCollection collection = (DefaultFeatureCollection) io.readFeatureCollection(stream);
		IIdentifier identifier = new Identifier(resourceURL.toString());
		if(withIndex)
			return new GTIndexedFeatureCollection(identifier, GTFeatureCollection.getGTCollection(identifier, collection), null);
		else
			return new GTFeatureCollection(identifier, collection, null);
	}

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_RESOURCE, new InputConnector(
                IN_RESOURCE,
                new MetadataForConnector(IN_RESOURCE.toString(), "Link to input GeoJSON"),
                new IDataConstraint[]{
                        new BindingConstraint(URILiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_WITH_INDEX, new InputConnector(
                IN_WITH_INDEX,
                new MetadataForConnector(IN_WITH_INDEX.toString(), "Flag: create spatial index"),
                new IDataConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false)));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_FEATURES, new OutputConnector(
                OUT_FEATURES,
                new MetadataForConnector(OUT_FEATURES.toString(), "Output feature collection"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null));
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
