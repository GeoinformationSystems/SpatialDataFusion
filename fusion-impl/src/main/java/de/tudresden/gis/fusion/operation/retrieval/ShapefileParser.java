package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;

import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class ShapefileParser extends AbstractOperation implements IDataRetrieval {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#ShapefileParser";

	@Override
	public void execute() {

		IDataResource shapeResource = (IDataResource) getInput(IN_RESOURCE);
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
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Parser for ESRI Shapefiles";
	}

	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_RESOURCE), "Shapefile resource",
						new IDataRestriction[]{
							ERestrictions.BINDING_IDATARESOURCE.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_WITH_INDEX), "if set true, a spatial index is build",
					new BooleanLiteral(true),
					new IDataRestriction[]{
						ERestrictions.BINDING_BOOLEAN.getRestriction()
					})
		);
		return inputs;				
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		Collection<IIODescription> outputs = new ArrayList<IIODescription>();
		outputs.add(new IODescription(
					new IRI(OUT_FEATURES), "Output features",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
					})
		);
		return outputs;
	}

}
