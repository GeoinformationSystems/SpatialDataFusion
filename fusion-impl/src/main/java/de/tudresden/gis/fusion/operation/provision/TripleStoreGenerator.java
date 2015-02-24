package de.tudresden.gis.fusion.operation.provision;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.sparql.modify.request.Target;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFTurtleEncoder;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataProvision;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class TripleStoreGenerator extends AbstractOperation implements IDataProvision {
	
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_TRIPLE_STORE = "IN_TRIPLE_STORE";
	private final String IN_CLEAR_STORE = "IN_CLEAR_STORE";	
	private final String IN_URI_BASE = "IN_URI_BASE";
	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
	
	private final String OUT_SUCCESS = "OUT_SUCCESS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#TripleStoreGenerator";
	
	@Override
	public void execute() throws ProcessException {
		
		//get input relations and uri pattern
		IComplexData inData = (IComplexData) getInput(IN_RELATIONS);
		IDataResource inTripleStore = (IDataResource) getInput(IN_TRIPLE_STORE);
		BooleanLiteral inRemove = (BooleanLiteral) getInput(IN_CLEAR_STORE);
		
		boolean bRemove = inputContainsKey(IN_CLEAR_STORE) ? inRemove.getValue() : false;
		String sTripleStoreURL = inTripleStore.getIdentifier().asString();
		
		StringLiteral uriBase = (StringLiteral) getInput(IN_URI_BASE);
		StringLiteral uriPrefixes = (StringLiteral) getInput(IN_URI_PREFIXES);
		
		//set prefixes
		Map<URI,String> prefixes = new LinkedHashMap<URI,String>();
		if(inputContainsKey(IN_URI_PREFIXES)){
			String[] prefixesArray = uriPrefixes.getIdentifier().split(";");
			for(int i=0; i<prefixesArray.length; i+=2){
				prefixes.put(URI.create(prefixesArray[i]), prefixesArray[i+1]);
			}
			//add base prefix
			if(inputContainsKey(IN_URI_BASE))
				prefixes.put(URI.create(uriBase.getIdentifier()), "base");
		}
		
		//insert data to triple store
		insertRDF(inData, sTripleStoreURL, bRemove, prefixes);
		
		//set outpur
		setOutput(OUT_SUCCESS, new BooleanLiteral(true));
		
	}
	
	private void insertRDF(IRDFTripleSet tripleSet, String sTripleStoreURL, boolean bRemove, Map<URI,String> prefixes){
		//empty triple store if requested
		if(bRemove){
			clearStore(sTripleStoreURL);
		}
		//update store
		updateStore(tripleSet, sTripleStoreURL, prefixes);
	}
	
	private void clearStore(String sTripleStoreURL){
		UpdateRequest request = new UpdateRequest().add(new UpdateClear(Target.ALL));
		updateStore(sTripleStoreURL, request);	
	}
	
	private void updateStore(String sTripleStoreURL, UpdateRequest request){
		UpdateExecutionFactory.createRemote(request, sTripleStoreURL).execute();
	}
	
	private String getPrefixHeader(Map<URI,String> prefixes){
		StringBuilder sHeader = new StringBuilder();
		//append prefixes
		for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
			sHeader.append("PREFIX " + prefix.getValue() + ": <" + prefix.getKey() + ">\n");
		}
		return sHeader.toString();
	}
		
	private void updateStore(IRDFTripleSet tripleSet, String sTripleStoreURL, Map<URI,String> prefixes) {
		//init string buffer
		StringBuilder sRequest = new StringBuilder();
		//insert data
		if(tripleSet instanceof IRDFCollection){
			List<String> rdfInserts = RDFTurtleEncoder.encodeTripleResource((IRDFCollection) tripleSet, null, prefixes, 1000);
			for(String insert : rdfInserts){
				sRequest.append(getPrefixHeader(prefixes));
				sRequest.append("INSERT DATA {\n");
				sRequest.append(insert);
				sRequest.append("}");
				updateStore(sTripleStoreURL, new UpdateRequest().add(sRequest.toString()));
				sRequest.setLength(0);
			}
		}
		else {
			sRequest.append(getPrefixHeader(prefixes));
			sRequest.append("INSERT DATA {\n");
			sRequest.append(RDFTurtleEncoder.encodeTripleResource(tripleSet, null, prefixes));
			sRequest.append("}");
			updateStore(sTripleStoreURL, new UpdateRequest().add(sRequest.toString()));
		}
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
		return "Triple Store Generator for relations";
	}

	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
				new IRI(IN_RELATIONS), "Input relations",
				new IDataRestriction[]{
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				})
		);
		inputs.add(new IODescription(
				new IRI(IN_TRIPLE_STORE), "triple store URI",
				new IDataRestriction[]{
					ERestrictions.BINDING_IDATARESOURCE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				})		
		);
		inputs.add(new IODescription(
				new IRI(IN_CLEAR_STORE), "if set true, the triple store is at first cleared",
				new BooleanLiteral(false),
				new IDataRestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				})
		);
		inputs.add(new IODescription(
				new IRI(IN_URI_BASE), "RDF URI base",
				new IDataRestriction[]{
					ERestrictions.BINDING_STRING.getRestriction()
				})
		);
		inputs.add(new IODescription(
				new IRI(IN_URI_PREFIXES), "RDF URI prefixes (CSV formatted)",
				new IDataRestriction[]{
					ERestrictions.BINDING_STRING.getRestriction()
				})
		);
		return inputs;				
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		Collection<IIODescription> outputs = new ArrayList<IIODescription>();
		outputs.add(new IODescription(
				new IRI(OUT_SUCCESS), "true, if insert was successful",
				new IDataRestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				})
		);
		return outputs;
	}
	
}
