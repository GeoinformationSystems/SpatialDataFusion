package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;
import java.io.InputStream;

import org.geotools.xml.Configuration;

import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
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

public class GMLParser extends AOperation implements IDataRetrieval {

	public static final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	public static final String OUT_FEATURES = "OUT_FEATURES";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};
	
	@Override
	public void execute() throws ProcessException {
		
		//get input url
		URILiteral gmlResource = (URILiteral) getInput(IN_RESOURCE);		
		BooleanLiteral inWithIndex = (BooleanLiteral) getInput(IN_WITH_INDEX);
		
		IIRI identifier = new IRI(gmlResource.getIdentifier());		
		boolean bWithIndex = inWithIndex == null ? false : inWithIndex.getValue();
		
		//parse feature collection		
		Configuration configuration;
		GTFeatureCollection wfsFC;
		InputStream gmlStream = null;
		try {
			gmlStream = identifier.asURL().openStream();
			configuration = new org.geotools.gml3.GMLConfiguration();
			if(bWithIndex)
				wfsFC = new GTIndexedFeatureCollection(identifier, gmlStream, configuration);
	        else
	        	wfsFC = new GTFeatureCollection(identifier, gmlStream, configuration);
			
		} catch (IOException e1) {
			try {
				gmlStream = identifier.asURL().openStream();
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
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Parser for GML";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_RESOURCE, "GML resource",
				new IIORestriction[]{
					ERestrictions.BINDING_URIRESOURCE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_WITH_INDEX, "if set true, a spatial index is build",
				new BooleanLiteral(true),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			)
		};				
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_FEATURES, "Output features",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
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
