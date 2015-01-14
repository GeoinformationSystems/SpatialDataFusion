package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.complex.OSMFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class OSMParser extends AbstractOperation implements IDataRetrieval {
	
	private final String IN_OSM_URL = "IN_OSM_URL";
	private final String OUT_OSM_COLLECTION = "OUT_OSM_COLLECTION";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#OSMParser";

	@Override
	public void execute() throws ProcessException {
		
		//get input url
		IDataResource osmResource = (IDataResource) getInput(IN_OSM_URL);
		
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
