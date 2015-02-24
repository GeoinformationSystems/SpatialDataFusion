package de.tudresden.gis.fusion.operation.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.Operations;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.IMeasurement;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class RelationAggregate extends AbstractMeasurementOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_OPERATIONS = "IN_OPERATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#RelationAggregate";
	
	Set<IMeasurement> availableOperations;
	Map<IIRI,Map<String,ISimpleData>> operationInputs;
	
	@Override
	public void execute() {
		
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
		for(Map.Entry<IIRI,Map<String,ISimpleData>> inOperation : operationInputs.entrySet()){
			IMeasurement operation = getOperation(inOperation.getKey());
			if(operation != null)
				relations = executeRelationOperation(operation, inReference, inTarget, relations, inOperation.getValue());
		}
		return relations;
	}
	
	/**
	 * get operation based on IRI
	 * @param key process iri
	 * @return operation instance or null, if no operation was found
	 */
	private IMeasurement getOperation(IIRI key) {
		for(IMeasurement operation : availableOperations){
			if(operation.getProfile().getIdentifier().equals(key) || operation.getProfile().getIdentifier().asString().endsWith(key.asString()))
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
	private IFeatureRelationCollection executeRelationOperation(IMeasurement operation, IFeatureCollection inReference, IFeatureCollection inTarget, IFeatureRelationCollection relations, Map<String,ISimpleData> literalInputs){
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
	 * initialize input operations 
	 * @param inOperations
	 */
	private void initOperationInputs(StringLiteral inOperations) {
		operationInputs = new HashMap<IIRI,Map<String,ISimpleData>>();
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
		//set IRI
		IIRI key = new IRI(param[0]);
		//set additional literals
		Map<String,ISimpleData> literalParam = new HashMap<String,ISimpleData>();
		for(int i=1; i<param.length; i+=2){
			literalParam.put(param[i], DataUtilities.encodeLiteral(param[i+1]));
		}
		//set input
		operationInputs.put(key, literalParam);
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
				new IRI(IN_OPERATIONS), "Operations (CSV: operation iri, literal inputs...)",
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
					new IRI(OUT_RELATIONS), "Output relations",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
					})
		);
		return outputs;
	}
	
	@Override
	protected Collection<IMeasurementDescription> getSupportedMeasurements() {
		Collection<IMeasurementDescription> measurements = new ArrayList<IMeasurementDescription>();
		for(IMeasurement operation : availableOperations){
			measurements.addAll(operation.getProfile().getSupportedMeasurements());
		}
		return measurements;
	}
	
	/**
	 * init available relation measurements
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initAvailableOperations() throws InstantiationException, IllegalAccessException {
		availableOperations = new LinkedHashSet<IMeasurement>();
		Set<Class<? extends IMeasurement>> operations = Operations.getAvalaibleMeasurementOperations();
		for(Class<? extends IMeasurement> clazz : operations) {
			//do not include this.class to prevent infinite initialization
			if(clazz.getPackage().getName().contains("aggregate"))
				continue;
			availableOperations.add(clazz.newInstance());
		}
	}

}
