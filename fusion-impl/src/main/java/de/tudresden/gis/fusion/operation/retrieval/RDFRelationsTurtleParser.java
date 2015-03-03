package de.tudresden.gis.fusion.operation.retrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFBasicTurtleDecoder;
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

public class RDFRelationsTurtleParser extends AOperation implements IDataRetrieval {
	
	public final String IN_RESOURCE = "IN_RESOURCE";
	public final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};
	
	private Map<String,URI> prefixes;
	IFeatureRelationCollection relations;
	
	@Override
	public void execute() {
		
		URILiteral rdfResource = (URILiteral) getInput(IN_RESOURCE);
		
		IIRI identifier = new IRI(rdfResource.getIdentifier());
		prefixes = new HashMap<String,URI>();
		relations = new FeatureRelationCollection();
		
		try {
			parseRelations(identifier, identifier.asURL().openStream());
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
        
		setOutput(OUT_RELATIONS, relations);
	}

	private void parseRelations(IIRI identifier, InputStream is) throws IOException, URISyntaxException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sBuilder = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	    	line = line.trim();
	    	if(line.startsWith("@")){
	    		String[] aPrefix = RDFBasicTurtleDecoder.getPrefix(line);
	    		prefixes.put(aPrefix[0], URI.create(aPrefix[1]));
	    	}
	    	else {
	    		sBuilder.append(line + "\n");
	    		if(line.endsWith(".")){
	    			FeatureRelation relation = new FeatureRelation((IRDFTripleSet) RDFBasicTurtleDecoder.decodeRDFResource(sBuilder.toString(), prefixes));
	    			if(relation != null)
	    				relations.addRelation(relation);
	    			sBuilder.setLength(0);
	    		}
	    	}
	    }
	    reader.close();
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
		return "Parser for RDF relations";
	}

	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
				new IODescription(
					IN_RESOURCE, "RDF relations resource",
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
				OUT_RELATIONS, "Output relations",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
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
