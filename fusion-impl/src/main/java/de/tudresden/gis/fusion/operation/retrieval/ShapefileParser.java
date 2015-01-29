package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;
import java.util.Collection;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;

import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class ShapefileParser extends AbstractOperation implements IDataRetrieval {
	
	private final String IN_SHAPE_RESOURCE = "IN_SHAPE_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#ShapefileParser";

	@Override
	public void execute() {

		IDataResource shapeResource = (IDataResource) getInput(IN_SHAPE_RESOURCE);
		BooleanLiteral inWithIndex = (BooleanLiteral) getInput(IN_WITH_INDEX);
		IIRI identifier = shapeResource.getIdentifier();
		
		boolean bWithIndex = inWithIndex == null ? false : inWithIndex.getValue();
		
		GTFeatureCollection shapeFC;
		try {
			ShapefileDataStore store = new ShapefileDataStore(shapeResource.getIdentifier().asURI().toURL());
	        String name = store.getTypeNames()[0];
	        SimpleFeatureSource source = store.getFeatureSource(name);
	        if(bWithIndex)
	        	shapeFC = new GTIndexedFeatureCollection(identifier, DataUtilities.collection(source.getFeatures().features()));
	        else
	        	shapeFC = new GTFeatureCollection(identifier, DataUtilities.collection(source.getFeatures().features()));
	        store.dispose();
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
        
		setOutput(OUT_FEATURES, shapeFC);
	}

	@Override
	public void setFilter(IFilter filter) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getProcessDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
