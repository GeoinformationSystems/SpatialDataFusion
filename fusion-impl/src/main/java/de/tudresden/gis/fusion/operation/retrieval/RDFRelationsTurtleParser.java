package de.tudresden.gis.fusion.operation.retrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class RDFRelationsTurtleParser extends AbstractOperation implements IDataRetrieval {
	
	public final String IN_RDF_RESOURCE = "IN_RDF_RESOURCE";
	public final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#RDFRelationsTurtleParser";
	
	private Map<String,URI> prefixes;
	IFeatureRelationCollection relations;
	
	@Override
	protected void execute() {
		IDataResource rdfResource = (IDataResource) getInput(IN_RDF_RESOURCE);
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
