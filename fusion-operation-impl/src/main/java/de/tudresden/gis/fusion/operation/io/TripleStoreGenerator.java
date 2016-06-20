package de.tudresden.gis.fusion.operation.io;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.hp.hpl.jena.sparql.modify.request.Target;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.rdf.ISubjectCollection;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.RDFTurtleEncoder;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IGenerator;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class TripleStoreGenerator extends AOperationInstance implements IGenerator {
	
	private final String IN_RDF = "IN_RDF";
	private final String IN_TRIPLE_STORE = "IN_TRIPLE_STORE";
	private final String IN_CLEAR_STORE = "IN_CLEAR_STORE";
	private final String IN_URI_BASE = "IN_URI_BASE";
	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
	
	private final String OUT_SUCCESS = "OUT_SUCCESS";
	
	private boolean bClear = false;
	private String sTripleStore;
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	@Override
	public void execute() throws ProcessException {

		//get base and prefixes
		URI base = inputContainsKey(IN_URI_BASE) ? URI.create(((StringLiteral) getInput(IN_URI_BASE)).resolve()) : null;
		Map<URI,String> prefixes = new LinkedHashMap<URI,String>();
		if(inputContainsKey(IN_URI_PREFIXES)){
			String[] prefixesArray = ((StringLiteral) getInput(IN_URI_PREFIXES)).resolve().split(";");
			for(int i=0; i<prefixesArray.length; i+=2){
				prefixes.put(URI.create(prefixesArray[i]), prefixesArray[i+1]);
			}
			//add base prefix
			if(inputContainsKey(IN_URI_BASE))
				prefixes.put(base, "base");
		}
		
		bClear = ((BooleanLiteral) getInput(IN_CLEAR_STORE)).resolve();
		sTripleStore = ((URILiteral) getInput(IN_TRIPLE_STORE)).getValue();
		
		IData data = getInput(IN_RDF);
		
		if(data instanceof ISubjectCollection || data instanceof ISubject)
			insertRDF(data, prefixes);
		else
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "cannot convert input to RDF");
		
		//set outpur
		setOutput(OUT_SUCCESS, new BooleanLiteral(true));
		
	}
	
	private void insertRDF(IData rdf, Map<URI,String> prefixes){
		//empty triple store if requested
		if(bClear){
			clearStore();
		}
		//update store
		updateStore(rdf, prefixes);
	}
	
	private void clearStore(){
		UpdateRequest request = new UpdateRequest().add(new UpdateClear(Target.ALL));
		updateStore(request);	
	}
	
	private void updateStore(UpdateRequest request){
		UpdateExecutionFactory.createRemote(request, sTripleStore).execute();
	}
	
	private String getPrefixHeader(Map<URI,String> prefixes){
		StringBuilder sHeader = new StringBuilder();
		//append prefixes
		for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
			sHeader.append("PREFIX " + prefix.getValue() + ": <" + prefix.getKey() + ">\n");
		}
		return sHeader.toString();
	}
	
	private void updateStore(IData rdf, Map<URI,String> prefixes) {
		//init string buffer
		StringBuilder sRequest = new StringBuilder();
		//insert data
		if(rdf instanceof ISubjectCollection){
			List<String> rdfInserts = RDFTurtleEncoder.encodeTripleResource((ISubjectCollection) rdf, null, prefixes, 1000);
			for(String insert : rdfInserts){
				sRequest.append(getPrefixHeader(prefixes));
				sRequest.append("INSERT DATA {\n");
				sRequest.append(insert);
				sRequest.append("}");
				updateStore(new UpdateRequest().add(sRequest.toString()));
				sRequest.setLength(0);
			}
		}
		else if(rdf instanceof ISubject){
			sRequest.append(getPrefixHeader(prefixes));
			sRequest.append("INSERT DATA {\n");
			sRequest.append(RDFTurtleEncoder.encodeTripleResource((ISubject) rdf, null, prefixes));
			sRequest.append("}");
			updateStore(new UpdateRequest().add(sRequest.toString()));
		}
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "RDF triple store generator";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Generator for W3C RDF Triple Store using SPARQL update";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		if(inputDescriptions == null){
			inputDescriptions = new HashSet<IInputDescription>();
			inputDescriptions.add(new InputDescription(IN_RDF, IN_RDF, "Input RDF triples)",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_RDF),
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_TRIPLE_STORE, IN_TRIPLE_STORE, "SPARQL Insert endpoint",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_TRIPLE_STORE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{StringLiteral.class})
					}));
			inputDescriptions.add(new InputDescription(IN_CLEAR_STORE, IN_CLEAR_STORE, "If true, the triple store is emptied first",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
					},
					new BooleanLiteral(false)));
			inputDescriptions.add(new InputDescription(IN_URI_BASE, IN_URI_BASE, "RDF Base uri",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{StringLiteral.class})
					}));
			inputDescriptions.add(new InputDescription(IN_URI_PREFIXES, IN_URI_PREFIXES, "RDF Prefixes",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{StringLiteral.class})
					}));
		}
		return inputDescriptions;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		if(outputDescriptions == null){
			outputDescriptions = new HashSet<IOutputDescription>();
			outputDescriptions.add(new OutputDescription(
					OUT_SUCCESS, OUT_SUCCESS, "True, if triples were inserted successfully",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_SUCCESS),
							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
					}));
		}
		return outputDescriptions;
	}

}
