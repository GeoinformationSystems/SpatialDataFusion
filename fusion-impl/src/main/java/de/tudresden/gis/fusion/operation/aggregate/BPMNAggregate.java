package de.tudresden.gis.fusion.operation.aggregate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.complex.BPMNModel;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.manage.Operations;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.misc.BPMNServiceTaskProxy;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class BPMNAggregate extends AOperation {
	
	private final String IN_BPMN = "IN_BPMN";	
	private final String OUT_RESULT = "OUT_RESULT";
	
	Map<String,IData> result = new HashMap<String,IData>();
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			
	};
	
	private BPMNModel inBPMN;
	private Set<IOperation> availableOperations;

	@Override
	protected void execute() {

		//initialize operations
		try {
			availableOperations = Operations.getNonAggregateOperations();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, "Cannot initialize available operations");
		}
		
		//get input
		inBPMN = (BPMNModel) getInput(IN_BPMN);
		
		//run basic validation on model
		validateModel();
		
		//execute
		executeBPMN();
		
		//set output (CSV with available output keys)
		String outputKeys = "";
		for(Map.Entry<String,IData> entry : result.entrySet()){
			outputKeys += entry.getKey() + ",";
			setOutput(entry.getKey(), entry.getValue());
		}
		setOutput(OUT_RESULT, new StringLiteral(outputKeys.substring(0, outputKeys.length() - 1)));
		
	}

	/**
	 * validate BPMN model
	 * @throws ProcessException if validation fails
	 */
	private void validateModel() throws ProcessException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * execute BPMN model
	 * @return execution result map
	 * @throws ProcessException if execution fails
	 */
	private void executeBPMN() {
		//initiate OperationProxy Map from BPMN
		Map<String,BPMNServiceTaskProxy> operationProxies = generateOperationProxies();
		//execute operations
		executeOperationProxies(operationProxies);
	}

	/**
	 * initiate operation proxy classes
	 * @return operation proxies
	 * @throws ProcessException if proxies cannot be initiated
	 */
	private Map<String,BPMNServiceTaskProxy> generateOperationProxies() {
		//get service tasks from bpmn model
		Collection<ServiceTask> serviceTasks = inBPMN.getBpmnModelInstance().getModelElementsByType(ServiceTask.class);
		//generate operation proxies
		Map<String,BPMNServiceTaskProxy> map = new HashMap<String,BPMNServiceTaskProxy>();
		for(ServiceTask serviceTask : serviceTasks){
			map.put(serviceTask.getId(), new BPMNServiceTaskProxy(serviceTask, availableOperations));
		}
		return map;
	}

	/**
	 * check if service task contributes to final result (links to end event)
	 * @param identifier service task identifier
	 * @return true, if service task links to end event
	 */
	private boolean isFinalResult(String identifier) {
		//get end event
		EndEvent endEvent = inBPMN.getBpmnModelInstance().getModelElementsByType(EndEvent.class).iterator().next();
		//check if identifier is linked by end event
		Collection<SequenceFlow> endEventIncoming = endEvent.getIncoming();
		for(SequenceFlow incoming : endEventIncoming){
			if(incoming.getId().contains(identifier))
				return true;
		}
		return false;
	}

	/**
	 * execute operation proxy classes
	 * @param operationProxies operation proxies
	 * @return execution result
	 * @throws ProcessException if execution fails
	 */
	private void executeOperationProxies(Map<String,BPMNServiceTaskProxy> operationProxies) {
		short exitCode = 42;
		Set<String> resultRequested = new HashSet<String>();
		while(exitCode == 42){
			boolean didExecuteSomething = false;
			for(BPMNServiceTaskProxy task : operationProxies.values()){
				//break, if task is already executable
				if(task.isExecutable()){
					//add to result, if connected to end event and executable
					if(isFinalResult(task.getIdentifier()) && !resultRequested.contains(task.getIdentifier())){
						result.putAll(task.getOutputs());
						resultRequested.add(task.getIdentifier());
						didExecuteSomething = true;
					}
					continue;
				}
				//set incomings for task
				Set<String> incomings = task.getIncoming();
				for(String incoming : incomings){
					BPMNServiceTaskProxy incomingTask = operationProxies.get(incoming);
					//break if incoming process is not executable; incomingTask is null for start and end event
					if(incomingTask == null || !incomingTask.isExecutable())
						break;
					//match
					for(String io : task.getInputAssociations().keySet()){
						if(incomingTask.hasOutptAssociation(io))
							task.setInputForAssociationId(io, incomingTask.getOutputForAssociationId(io));
					}
					didExecuteSomething = true;
//					System.out.println(incomingTask.getIdentifier() + " : " + task.getIdentifier());
				}
			}
			if(!didExecuteSomething)
				if(allTasksExecuted(operationProxies))
					exitCode = 0;
				else
					exitCode = 1;
		}
		if(exitCode != 0)
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, "BPMN Process with errors: Exit code " + exitCode);
	}
	
	private boolean allTasksExecuted(Map<String,BPMNServiceTaskProxy> operationProxies) {
		for(BPMNServiceTaskProxy task : operationProxies.values()){
			if(!task.isExecuted())
				return false;
		}
		return true;
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
	protected String getProcessAbstract() {
		return "Executes fusion processes based on BPMN model";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_BPMN, "BPMN Model",
					new IIORestriction[]{
							ERestrictions.BINDING_BPMN.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					OUT_RESULT, "Operation result (CSV with available output keys)",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_STRING.getRestriction()
					}
			)
		};
	}

}
