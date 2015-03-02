package de.tudresden.gis.fusion.operation.provision;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.sparql.modify.request.Target;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFTurtleEncoder;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataProvision;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class TripleStoreGenerator extends AOperation implements IDataProvision {
	
	private final String IN_RDF = "IN_RDF";
	private final String IN_TRIPLE_STORE = "IN_TRIPLE_STORE";
	private final String IN_CLEAR_STORE = "IN_CLEAR_STORE";	
	private final String IN_URI_BASE = "IN_URI_BASE";
	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
	
	private final String OUT_SUCCESS = "OUT_SUCCESS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.PROVISION.resource()
	};
	
	@Override
	public void execute() throws ProcessException {
		
		//get input relations and uri pattern
		IComplexData inData = (IComplexData) getInput(IN_RDF);
		URILiteral inTripleStore = (URILiteral) getInput(IN_TRIPLE_STORE);
		BooleanLiteral inRemove = (BooleanLiteral) getInput(IN_CLEAR_STORE);
		
		boolean bRemove = inputContainsKey(IN_CLEAR_STORE) ? inRemove.getValue() : false;
		String sTripleStoreURL = inTripleStore.getIdentifier();
		
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
		insertRDF(inData.getRDFRepresentation(), sTripleStoreURL, bRemove, prefixes);
		
		//set outpur
		setOutput(OUT_SUCCESS, new BooleanLiteral(true));
		
	}
	
	private void insertRDF(IRDFRepresentation rdf, String sTripleStoreURL, boolean bRemove, Map<URI,String> prefixes){
		//empty triple store if requested
		if(bRemove){
			clearStore(sTripleStoreURL);
		}
		//update store
		updateStore(rdf, sTripleStoreURL, prefixes);
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
		
	private void updateStore(IRDFRepresentation rdf, String sTripleStoreURL, Map<URI,String> prefixes) {
		//init string buffer
		StringBuilder sRequest = new StringBuilder();
		//insert data
		if(rdf instanceof IRDFCollection){
			List<String> rdfInserts = RDFTurtleEncoder.encodeTripleResource((IRDFCollection) rdf, null, prefixes, 1000);
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
			sRequest.append(RDFTurtleEncoder.encodeTripleResource(rdf, null, prefixes));
			sRequest.append("}");
			updateStore(sTripleStoreURL, new UpdateRequest().add(sRequest.toString()));
		}
	}

	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	protected String getProcessDescription() {
		return "Triple Store Generator for relations";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_RDF, "Input RDF data",
					new IIORestriction[]{
							ERestrictions.BINDING_ICOMPLEX.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
					IN_URI_BASE, "RDF URI base",
					new IIORestriction[]{
							ERestrictions.BINDING_STRING.getRestriction()
					}
			),
			new IODescription(
					IN_URI_PREFIXES, "RDF URI prefixes (CSV formatted)",
					new IIORestriction[]{
							ERestrictions.BINDING_STRING.getRestriction()
					}
			),
			new IODescription(
					IN_CLEAR_STORE, "set true, if the triple store should be emptied first",
					new BooleanLiteral(false),
					new IIORestriction[]{
							ERestrictions.BINDING_BOOLEAN.getRestriction()
					}
			),
			new IODescription(
					IN_TRIPLE_STORE, "relations that do not satisfy the threshold are dropped",
					new BooleanLiteral(false),
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
			new IODescription(
					OUT_SUCCESS, "true, if insert operation was successful",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_BOOLEAN.getRestriction()
					}
			)
		};
	}
	
}
