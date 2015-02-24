package de.tudresden.gis.fusion.operation.retrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.RDFTurtleDecoder;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class RDFRelationsTurtleParser extends AbstractOperation implements IDataRetrieval {
	
	public final String IN_RESOURCE = "IN_RESOURCE";
	public final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#RDFRelationsTurtleParser";
	
	private Map<String,URI> prefixes;
	IFeatureRelationCollection relations;
	
	@Override
	public void execute() {
		IDataResource rdfResource = (IDataResource) getInput(IN_RESOURCE);
		IIRI identifier = rdfResource.getIdentifier();
		
		prefixes = new HashMap<String,URI>();
		relations = new FeatureRelationCollection();
		
		try {
			parseRelations(identifier, rdfResource.getIdentifier().asURI().toURL().openStream());
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
	    		String[] aPrefix = RDFTurtleDecoder.getPrefix(line);
	    		prefixes.put(aPrefix[0], URI.create(aPrefix[1]));
	    	}
	    	else {
	    		sBuilder.append(line + "\n");
	    		if(line.endsWith(".")){
	    			FeatureRelation relation = new FeatureRelation((IRDFTripleSet) RDFTurtleDecoder.decodeRDFResource(sBuilder.toString(), prefixes));
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
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Parser for RDF relations";
	}

	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_RESOURCE), "RDF relations resource",
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
					new IRI(OUT_RELATIONS), "Output relations",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
					})
		);
		return outputs;
	}

}
