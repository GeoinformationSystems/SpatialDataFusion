package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.geotools.xml.Configuration;

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

public class GMLParser extends AbstractOperation implements IDataRetrieval {

	public static final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	public static final String OUT_FEATURES = "OUT_FEATURES";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#GMLParser";
	
	@Override
	public void execute() throws ProcessException {
		
		//get input url
		IDataResource gmlResource = (IDataResource) getInput(IN_RESOURCE);
		BooleanLiteral inWithIndex = (BooleanLiteral) getInput(IN_WITH_INDEX);
		IIRI identifier = gmlResource.getIdentifier();
		
		boolean bWithIndex = inWithIndex == null ? false : inWithIndex.getValue();
		
		//parse feature collection		
		Configuration configuration;
		GTFeatureCollection wfsFC;
		InputStream gmlStream = null;
		try {
			gmlStream = gmlResource.getIdentifier().asURI().toURL().openStream();
			configuration = new org.geotools.gml3.GMLConfiguration();
			if(bWithIndex)
				wfsFC = new GTIndexedFeatureCollection(identifier, gmlStream, configuration);
	        else
	        	wfsFC = new GTFeatureCollection(identifier, gmlStream, configuration);
			
		} catch (IOException e1) {
			try {
				gmlStream = gmlResource.getIdentifier().asURI().toURL().openStream();
				configuration = new org.geotools.gml2.GMLConfiguration();
				wfsFC = new GTFeatureCollection(identifier, gmlStream, configuration);
			} catch (IOException e2) {
				throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e2);
			}
		} finally {
			try {
				gmlStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//set output
		setOutput(OUT_FEATURES, wfsFC);
		
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
		return "Parser for GML";
	}

	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_RESOURCE), "GML resource",
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
