package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import de.tudresden.gis.fusion.data.complex.OSMFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;

public class OSMParser extends AOperation implements IDataRetrieval {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String OUT_OSM_COLLECTION = "OUT_OSM_COLLECTION";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};
	
	@Override
	public void execute() throws ProcessException {
		
		//get input url
		URILiteral osmResource = (URILiteral) getInput(IN_RESOURCE);
		
		//parse OSM collection
		try {
			OSMFeatureCollection osmCollection = new OSMFeatureCollection(new IRI(osmResource.getIdentifier()));
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
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Parser for OSM XML";
	}
	
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
				new IODescription(
					IN_RESOURCE, "OSM XML resource",
					new IIORestriction[]{
						ERestrictions.BINDING_URIRESOURCE.getRestriction(),
						ERestrictions.MANDATORY.getRestriction()
					}
				),
		};			
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription (
				OUT_OSM_COLLECTION, "OSM output collection",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_OSMFEATUReCOLLECTION.getRestriction()
				}
			)
		};
	}
	
	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}
	
}
