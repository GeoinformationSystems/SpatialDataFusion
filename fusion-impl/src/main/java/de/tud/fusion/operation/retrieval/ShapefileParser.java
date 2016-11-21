package de.tud.fusion.operation.retrieval;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import de.tud.fusion.data.feature.FeatureCollection;
import de.tud.fusion.data.feature.IndexedFeatureCollection;
import de.tud.fusion.data.feature.geotools.GTFeature;
import de.tud.fusion.data.feature.geotools.GTFeatureCollection;
import de.tud.fusion.data.literal.BooleanLiteral;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.operation.AbstractOperation;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.InputConnector;
import de.tud.fusion.operation.description.OutputConnector;

public class ShapefileParser extends AbstractOperation {
	
	public final static String PROCESS_ID = ShapefileParser.class.getSimpleName();
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private Set<IInputConnector> inputConnectors;
	private Set<IOutputConnector> outputConnectors;

	/**
	 * constructor
	 */
	public ShapefileParser() {
		super(PROCESS_ID);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		IInputConnector indexConnector = getInputConnector(IN_WITH_INDEX);
		//get data
		URL resourceURL;
		try {
			resourceURL = ((URILiteral) resourceConnector.getData()).resolve().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Shapefile source is no valid URL: ", e);
		}
		boolean withIndex = ((BooleanLiteral) indexConnector.getData()).resolve();
		//parse features
		FeatureCollection<?> features;
		try {
			features = parseShape(resourceURL, withIndex);
		} catch (IOException e) {
			throw new RuntimeException("Could not parse Shapefile", e);
		}
		//set output connector
		setOutputConnector(OUT_FEATURES, features);
	}

	/**
	 * parse shapefile
	 * @param resourceURL shapefile URL
	 * @param withIndex flag: return indexed collection
	 * @return feature collection
	 * @throws IOException
	 */
	private FeatureCollection<?> parseShape(URL resourceURL, boolean withIndex) throws IOException {
		ShapefileDataStore store = new ShapefileDataStore(resourceURL);
		store.setCharset(StandardCharsets.UTF_8);
        String name = store.getTypeNames()[0];
        SimpleFeatureSource source = store.getFeatureSource(name);
        SimpleFeatureCollection shapeFC = DataUtilities.collection(source.getFeatures().features());
        store.dispose();
        if(withIndex)
        	return new IndexedFeatureCollection<GTFeature>(resourceURL.toString(), GTFeatureCollection.getGTCollection(resourceURL.toString(), shapeFC), null);
        else
        	return new GTFeatureCollection(resourceURL.toString(), shapeFC, null);
	}
	
	@Override
	public Set<IInputConnector> getInputConnectors() {
		if(inputConnectors != null)
			return inputConnectors;
		//generate descriptions
		inputConnectors = new HashSet<IInputConnector>();
		inputConnectors.add(new InputConnector(
				IN_RESOURCE, IN_RESOURCE, "Link to input Shapefile",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class),
						new MandatoryConstraint()},
				null,
				null));
		inputConnectors.add(new InputConnector(
				IN_WITH_INDEX, IN_WITH_INDEX, "Flag: create spatial index",
				new IDataConstraint[]{
						new BindingConstraint(BooleanLiteral.class)},
				null,
				new BooleanLiteral(false)));
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
		return "Shapefile Parser";
	}

	@Override
	public String getProcessAbstract() {
		return "Parser for Shapefile format";
	}

}
