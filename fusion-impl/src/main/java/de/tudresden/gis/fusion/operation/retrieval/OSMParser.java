package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.complex.OSMFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class OSMParser extends AbstractOperation implements IDataRetrieval {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String OUT_OSM_COLLECTION = "OUT_OSM_COLLECTION";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#OSMParser";

	@Override
	public void execute() throws ProcessException {
		
		//get input url
		IDataResource osmResource = (IDataResource) getInput(IN_RESOURCE);
		
		//parse OSM collection
		try {
			OSMFeatureCollection osmCollection = new OSMFeatureCollection(osmResource.getIdentifier());
			//set output
			setOutput(OUT_OSM_COLLECTION, osmCollection);
			
		} catch (XMLStreamException xmle) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, xmle);
		} catch(IOException ioe){
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, ioe);
		}
		
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
		return "Parser for OSM XML";
	}

	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_RESOURCE), "OSM XML resource",
						new IDataRestriction[]{
							ERestrictions.BINDING_IDATARESOURCE.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
						})
		);
		return inputs;				
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		Collection<IIODescription> outputs = new ArrayList<IIODescription>();
		outputs.add(new IODescription(
					new IRI(OUT_OSM_COLLECTION), "OSM output collection",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_OSMFEATUReCOLLECTION.getRestriction()
					})
		);
		return outputs;
	}
	
}
