package de.tudresden.gis.fusion.operation.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.Operations;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class OperationAggregate extends AbstractOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_OPERATIONS = "IN_OPERATIONS";
	
	private final String OUT_OUTPUT = "OUT_OUTPUT";
	
	private final String LITERAL_OBJECT = "LITERAL";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#OperationAggregate";
	
	Map<String,OperationProxy> operations;
	List<OperationProxy> sortedOperations;
	Set<IOperation> availableOperations;
	
	//input collections are set global
	IFeatureCollection inReference;
	IFeatureCollection inTarget;

	@Override
	protected void execute() {
		
		//get input
		inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		inTarget = (IFeatureCollection) getInput(IN_TARGET);
		StringLiteral inOperations = (StringLiteral) getInput(IN_OPERATIONS);
		
		initOperationInputs(inOperations);
		Map<String,IData> results = executeOperations(inReference, inTarget);
		String outputKeys = "";
		for(Map.Entry<String,IData> result : results.entrySet()){
			outputKeys += result.getKey() + ",";
			setOutput(result.getKey(), result.getValue());
		}
		setOutput(OUT_OUTPUT, new StringLiteral(outputKeys.substring(0, outputKeys.length() - 1)));
		
	}
	
	private void initOperationInputs(StringLiteral inOperations) {

		operations = new LinkedHashMap<String,OperationProxy>();
		String[] sOperations = inOperations.getValue().split(";");
		for(String sOperation : sOperations){
			setOperationInput(sOperation);
		}
		//sort operations
		sortMap();
	}
	
	

	private void sortMap() {
		sortedOperations = new ArrayList<OperationProxy>();
		loop:
		for(Map.Entry<String,OperationProxy> operation : operations.entrySet()){
			if(sortedOperations.isEmpty()){
				sortedOperations.add(operation.getValue());
				continue;
			}
			int i = 0;			
			for(OperationProxy tmpOp : sortedOperations){
				if(tmpOp.compareTo(operation.getValue()) > 0){
					sortedOperations.add(i, operation.getValue());
					continue loop;
				}
				i++;
			}
			//append operation if not yet set
			sortedOperations.add(operation.getValue());
		}		
	}

	private void setOperationInput(String sOperation) {
		String[] param = sOperation.split(",");
		//return if param length is null or
		if(param.length == 0)
			return;
		//get process, add to operations list if not yet present
		String operationKey = param[0];
		OperationProxy operation = getOperation(operationKey);
		if(!operations.containsKey(operationKey))
			operations.put(operationKey, operation);
		//set process inputs
		for(int i=1; i<param.length; i+=3){
			setOperationInput(operation, param[i], param[i+1], param[i+2]);
		}
	}

	private void setOperationInput(OperationProxy process, String iParam, String oProcessKey, String oParam) {
		//get precursor, add to operations if not yet in list
		OperationProxy precursor = getOperation(oProcessKey);
		//set value, if literal
		if(precursor.isDataObject()){
			precursor.setOutput(LITERAL_OBJECT, DataUtilities.encodeLiteral(oParam));
			oParam = LITERAL_OBJECT;
		}
		if(!operations.containsKey(precursor.getKey()))
			operations.put(precursor.getKey(), precursor);
		//set precursor for process
		process.setPrecursor(precursor, oParam, iParam);
		//set successor for oProcess
		precursor.setSuccessor(process);
	}
	
	/**
	 * get operation based on IRI
	 * @param key process iri
	 * @return operation instance or null, if no operation was found
	 */
	private OperationProxy getOperation(String key) {
		if(key.equals(LITERAL_OBJECT))
			return new OperationProxy(key + UUID.randomUUID());
		if(operations.containsKey(key))
			return operations.get(key);
		for(IOperation operation : availableOperations){
			if(operation.getProfile().getIdentifier().equals(key) || operation.getProfile().getIdentifier().asString().endsWith(key))
				return new OperationProxy(key, operation);
		}
		return null;
	}

	private Map<String,IData> executeOperations(IFeatureCollection inReference, IFeatureCollection inTarget) {
		for(OperationProxy operation : sortedOperations){
			operation.execute();
		}
		//return output of last process
		return sortedOperations.get(sortedOperations.size() - 1).getOutputs();
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
		return "Aggregates input relation processes to form feature relations";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_REFERENCE), "Reference features",
						new IDataRestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET), "Target features",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					})
		);
		inputs.add(new IODescription(
				new IRI(IN_OPERATIONS), "Operations (CSV: operation iri, operation io, in sequence of execution)",
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
					new IRI(OUT_OUTPUT), "Operations output (CSV with available output keys)",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_STRING.getRestriction()
					})
		);
		return outputs;
	}
	
	@Override
	protected void initDescription() {
		try {
			initAvailableOperations();
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT);
		}
		super.initDescription();
	}
	
	/**
	 * init available relation measurements
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initAvailableOperations() throws InstantiationException, IllegalAccessException {
		availableOperations = new LinkedHashSet<IOperation>();
		Set<Class<? extends IOperation>> operations = Operations.getAvalaibleOperations();
		for(Class<? extends IOperation> clazz : operations) {
			//do not include this.class to prevent infinite initialization
			if(clazz.getPackage().getName().contains("aggregate"))
				continue;
			availableOperations.add(clazz.newInstance());
		}
	}
	
	/**
	 * operation proxy class
	 */
	private class OperationProxy implements Comparable<OperationProxy> {
		
		private String key;
		private Map<String,IData> inputs = new HashMap<String,IData>();
		private Map<String,IData> outputs = new HashMap<String,IData>();
		private IOperation operation;
		private boolean isDataObject = false;
		private Map<OperationProxy,String[]> precursors = new HashMap<OperationProxy,String[]>();
		private Set<OperationProxy> successors = new HashSet<OperationProxy>();
		
		public OperationProxy(String key, IOperation operation){
			this.key = key;
			this.operation = operation;
			//set input collections
			setInput(IN_REFERENCE, inReference);
			setInput(IN_TARGET, inTarget);
		}
		
		public OperationProxy(String key){
			this.key = key;
			isDataObject = true;
		}
		
		public void execute() {
			if(isDataObject())
				return;
			for(Map.Entry<OperationProxy,String[]> precursor : precursors.entrySet()){
				String[] io = precursor.getValue();
				Map<String,IData> precursorResult = precursor.getKey().getOutputs();
				if(precursorResult.get(io[1]) == null)
					throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, "Precessor input for " + io[1] + " is null");
				setInput(io[0], precursorResult.get(io[1]));
			}
			outputs = operation.execute(inputs);
		}
		
		public String getKey() {
			return key;
		}
		
		public boolean isDataObject() {
			return isDataObject;
		}
		
		public void setInput(String key, IData input){
			this.inputs.put(key, input);
		}
		
		public void setPrecursor(OperationProxy proxy, String oParam, String iParam){
			this.precursors.put(proxy, new String[]{iParam, oParam});
		}
		
		public void setSuccessor(OperationProxy proxy){
			this.successors.add(proxy);
		}
		
		public void setOutput(String key, IData input) {
			this.outputs.put(key, input);
		}
		
		public Map<String,IData> getOutputs() {
			return this.outputs;
		}

		@Override
		public int compareTo(OperationProxy operationProxy) {
			if(precursors.containsKey(operationProxy))
				return 1;
			if(successors.contains(operationProxy))
				return -1;
			else
				return 0;
		}
		
	}
	
}
	

