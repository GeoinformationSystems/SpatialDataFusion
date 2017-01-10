package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ShapefileParser extends AbstractOperation {

	private static final IIdentifier PROCESS = new Identifier(ShapefileParser.class.getSimpleName());

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
	private final static IIdentifier IN_WITH_INDEX = new Identifier("IN_WITH_INDEX");

	private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");

	/**
	 * constructor
	 */
	public ShapefileParser() {
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
		AbstractFeatureCollection<?> features;
		try {
			features = parseShape(resourceURI.toURL(), withIndex);
		} catch (IOException e) {
			throw new RuntimeException("Could not parse Shapefile", e);
		}
		//set output connector
		connectOutput(OUT_FEATURES, features);
	}

	/**
	 * parse shapefile
	 * @param resourceURL shapefile URL
	 * @param withIndex flag: return indexed collection
	 * @return feature collection
	 * @throws IOException
	 */
	private AbstractFeatureCollection<?> parseShape(URL resourceURL, boolean withIndex) throws IOException {
		ShapefileDataStore store = new ShapefileDataStore(resourceURL);
		store.setCharset(StandardCharsets.UTF_8);
        String name = store.getTypeNames()[0];
        SimpleFeatureSource source = store.getFeatureSource(name);
        SimpleFeatureCollection shapeFC = DataUtilities.collection(source.getFeatures().features());
        store.dispose();
		IIdentifier identifier = new Identifier(resourceURL.toString());
        if(withIndex)
        	return new GTIndexedFeatureCollection(identifier, GTFeatureCollection.getGTCollection(identifier, shapeFC), null);
        else
        	return new GTFeatureCollection(identifier, shapeFC, null);
	}

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_RESOURCE, new InputConnector(
                IN_RESOURCE,
                new MetadataForConnector(IN_RESOURCE.toString(), "Link to input Shapefile"),
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
		return "Shapefile Parser";
	}

	@Override
	public String getProcessAbstract() {
		return "Parser for Shapefile format";
	}

}
