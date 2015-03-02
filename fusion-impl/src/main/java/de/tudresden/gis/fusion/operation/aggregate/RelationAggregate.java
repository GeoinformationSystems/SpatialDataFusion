package de.tudresden.gis.fusion.operation.aggregate;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.manage.Operations;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class RelationAggregate extends AOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_OPERATIONS = "IN_OPERATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource()
	};
	
	Set<IMeasurementOperation> availableOperations;
	Map<String,Map<String,ISimpleData>> operationInputs;
	
	@Override
	public void execute() {
		
		//initialize operations
		try {
			initAvailableOperations();
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, "Cannot initiate available operations");
		}
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		StringLiteral inOperations = (StringLiteral) getInput(IN_OPERATIONS);
		
		initOperationInputs(inOperations);
		IFeatureRelationCollection relations = executeOperations(inReference, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	/**
	 * aggregate operations
	 * @param inReference reference features
	 * @param inTarget target features
	 * @return relations
	 */
	private IFeatureRelationCollection executeOperations(IFeatureCollection inReference, IFeatureCollection inTarget) {
		//run operations
		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
		for(Map.Entry<String,Map<String,ISimpleData>> inOperation : operationInputs.entrySet()){
			IMeasurementOperation operation = getOperation(inOperation.getKey());
			if(operation != null)
				relations = executeRelationOperation(operation, inReference, inTarget, relations, inOperation.getValue());
		}
		return relations;
	}
	
	/**
	 * get operation based on process name
	 * @param key process iri
	 * @return operation instance or null, if no operation was found
	 */
	private IMeasurementOperation getOperation(String name) {
		for(IMeasurementOperation operation : availableOperations){
			if(operation.getProfile().getProcessName().equalsIgnoreCase(name) || operation.getProfile().getProcessName().toLowerCase().endsWith(name.toLowerCase()))
				return operation;
		}
		return null;
	}

	/**
	 * execute relation measurement process
	 * @param operation operation instance
	 * @param inReference reference features
	 * @param inTarget target features
	 * @param relations input relations
	 * @param literalInputs literal operation inputs
	 * @return relations
	 */
	private IFeatureRelationCollection executeRelationOperation(IMeasurementOperation operation, IFeatureCollection inReference, IFeatureCollection inTarget, IFeatureRelationCollection relations, Map<String,ISimpleData> literalInputs){
		Map<String,IData> input = new HashMap<String,IData>();
		//set feature inputs 
		input.put("IN_REFERENCE", inReference);
		input.put("IN_TARGET", inTarget);
		//set relation input
		if(relations != null && relations.size() > 0)
			input.put("IN_RELATIONS", relations);
		//set literal inputs
		for(Map.Entry<String,ISimpleData> literalInput : literalInputs.entrySet()){
			input.put(literalInput.getKey(), literalInput.getValue());
		}
		//return relations
		Map<String,IData> output = operation.execute(input);
		return (IFeatureRelationCollection) output.get("OUT_RELATIONS");
	}
	
	/**
	 * initialize input operations 
	 * @param inOperations
	 */
	private void initOperationInputs(StringLiteral inOperations) {
		operationInputs = new HashMap<String,Map<String,ISimpleData>>();
		String[] sOperations = inOperations.getValue().split(";");
		for(String sOperation : sOperations){
			setOperationInput(sOperation);
		}
	}

	/**
	 * initialize input operation
	 * @param sOperation
	 */
	private void setOperationInput(String sOperation) {
		String[] param = sOperation.split(",");
		//return if param length is null or with even length
		if(param.length == 0 || param.length % 2 == 0)
			return;
		//set key
		String key = param[0];
		//set additional literals
		Map<String,ISimpleData> literalParam = new HashMap<String,ISimpleData>();
		for(int i=1; i<param.length; i+=2){
			literalParam.put(param[i], DataUtilities.encodeLiteral(param[i+1]));
		}
		//set input
		operationInputs.put(key, literalParam);
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
		return "Aggregates input relation processes to form feature relations";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_REFERENCE, "Reference features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
					IN_TARGET, "Target features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
					IN_OPERATIONS, "Operations (CSV: operation iri, literal inputs...)",
					new IIORestriction[]{
							ERestrictions.BINDING_STRING.getRestriction()
					}
			),
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_RELATIONS, "Output relations",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
			)
		};
	}
	
	/**
	 * init available relation measurements
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initAvailableOperations() throws InstantiationException, IllegalAccessException {
		availableOperations = new LinkedHashSet<IMeasurementOperation>();
		Set<Class<? extends IMeasurementOperation>> operations = Operations.getRelationMeasurementOperations();
		for(Class<? extends IMeasurementOperation> clazz : operations) {
			//do not include this.class to prevent infinite initialization
			if(clazz.getPackage().getName().contains("aggregate"))
				continue;
			availableOperations.add(clazz.newInstance());
		}
	}

}
