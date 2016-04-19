package de.tudresden.gis.fusion.operation.io;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;

import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IParser;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class ShapefileParser extends AOperationInstance implements IParser {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	@Override
	public void execute() throws ProcessException {
		
		URILiteral shapeResource = (URILiteral) input(IN_RESOURCE);
		boolean bWithIndex = inputContainsKey(IN_WITH_INDEX) ? ((BooleanLiteral) input(IN_WITH_INDEX)).resolve() : false;
		
		GTFeatureCollection shapeFC;
		try {
			URL resourceURL = shapeResource.resolve().toURL();
			ShapefileDataStore store = new ShapefileDataStore(resourceURL);
			store.setCharset(StandardCharsets.UTF_8);
	        String name = store.getTypeNames()[0];
	        SimpleFeatureSource source = store.getFeatureSource(name);
	        if(bWithIndex)
	        	shapeFC = new GTIndexedFeatureCollection(DataUtilities.collection(source.getFeatures().features()));
	        else
	        	shapeFC = new GTFeatureCollection(DataUtilities.collection(source.getFeatures().features()));
	        store.dispose();
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Error while parsing Shapefile", e);
		}
        
		setOutput(OUT_FEATURES, shapeFC);
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Shapefile parser";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Parser for ESRI Shapefile format";
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
