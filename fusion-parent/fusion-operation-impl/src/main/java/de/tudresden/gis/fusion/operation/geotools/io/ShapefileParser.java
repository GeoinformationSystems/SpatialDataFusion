package de.tudresden.gis.fusion.operation.geotools.io;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
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
		boolean bWithIndex = inputContainsKey(IN_WITH_INDEX) ? ((BooleanLiteral) input(IN_WITH_INDEX)).value() : false;
		
		GTFeatureCollection shapeFC;
		try {
			URL resourceURL = shapeResource.value().toURL();
			ShapefileDataStore store = new ShapefileDataStore(resourceURL);
	        String name = store.getTypeNames()[0];
	        SimpleFeatureSource source = store.getFeatureSource(name);
	        if(bWithIndex)
	        	shapeFC = new GTIndexedFeatureCollection(new IRI(resourceURL), DataUtilities.collection(source.getFeatures().features()), null);
	        else
	        	shapeFC = new GTFeatureCollection(new IRI(resourceURL), DataUtilities.collection(source.getFeatures().features()), null);
	        store.dispose();
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Error while parsing Shapefile", e);
		}
        
		setOutput(OUT_FEATURES, shapeFC);
	}
	
	@Override
	public IRI processIdentifier() {
		return new IRI(this.getClass().getSimpleName());
	}

	@Override
	public String processTitle() {
		return "Shapefile parser";
	}

	@Override
	public String processAbstract() {
		return "Parser for ESRI Shapefile format";
	}

	@Override
	public Collection<IProcessConstraint> processConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IInputDescription> inputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IOutputDescription> outputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
