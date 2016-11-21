package de.tud.fusion.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.IdentifiableObject;
import de.tud.fusion.data.description.MeasurementDescription;
import de.tud.fusion.data.literal.LongLiteral;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IIOConnector;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOperationDescription;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.IProcessDescription;
import de.tud.fusion.operation.description.OperationDescription;
import de.tud.fusion.operation.description.OutputConnector;
import de.tud.fusion.operation.description.ProcessDescription;

/**
 * Abstract operation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractOperation extends IdentifiableObject implements IOperation {
	
	public static final String OUT_START = "OUT_START";
	public static final String OUT_RUNTIME = "OUT_RUNTIME";

	private Set<IInputConnector> inputConnectors;
	private Set<IOutputConnector> outputConnectors;
	
	/**
	 * constructor
	 * @param identifier operation identifier
	 */
	public AbstractOperation(String identifier){
		super(identifier);
	}

	@Override
	public Map<String,IData> execute(Map<String,IData> inputs) {
		//init connectors
		inputConnectors = getInputConnectors();
		outputConnectors = getAmendedOutputConnectors();
		//set inputs
		setInputConnectors(inputs);
		//validateInputs
		validateInputs();
		//set start time
		setStartTime();
		//execute (set output connectors)
		execute();
		//validateOutputs
		validateOutputs();
		//set runtime
		setRuntime();
		//return result
		return getOutputs();
	}

	/**
	 * get input connectors
	 * @return input connectors
	 */
	public abstract Set<IInputConnector> getInputConnectors();
	
	/**
	 * get output connectors
	 * @return output connectors
	 */
	public abstract Set<IOutputConnector> getOutputConnectors();
	
	/**
	 * get amended output connectors (with start time and runtime)
	 * @return output connectors
	 */
	public Set<IOutputConnector> getAmendedOutputConnectors(){
		Set<IOutputConnector> outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.addAll(getOutputConnectors());
		outputConnectors.add(new OutputConnector(
				OUT_START, OUT_START, "Start time of the operation",
				new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(LongLiteral.class)},
				null));
		outputConnectors.add(new OutputConnector(
				OUT_RUNTIME, OUT_RUNTIME, "Runtime of the operation",
				new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(LongLiteral.class)},
				null));
		return outputConnectors;
	}

	/**
	 * set input connectors
	 * @param inputs process inputs
	 */
	private void setInputConnectors(Map<String,IData> inputs) {
		for(Map.Entry<String,IData> input : inputs.entrySet()){
			setInputConnector(input.getKey(), input.getValue());
		}
	}
	
	/**
	 * set input connector
	 * @param input process input
	 */
	private void setInputConnector(String identifier, IData input) {
		IInputConnector connector = getInputConnector(identifier);
		if(connector != null)
			connector.connect(input);	
	}
	
	/**
	 * validate input connectors
	 */
	private void validateInputs() {
		for(IInputConnector inputConnector : getInputConnectors()){
			inputConnector.validate();
		}
	}
	
	/**
	 * set output connectors
	 * @param outputs process outputs
	 */
	protected void setOutputConnectors(Map<String,IData> outputs) {
		for(Map.Entry<String,IData> output : outputs.entrySet()){
			setOutputConnector(output.getKey(), output.getValue());
		}
	}
	
	/**
	 * set output connector
	 * @param output process output
	 */
	protected void setOutputConnector(String identifier, IData output) {
		IOutputConnector connector = getOutputConnector(identifier);
		if(connector != null)
			connector.connect(output);
	}
	
	/**
	 * get input connector
	 * @return process input
	 */
	protected IInputConnector getInputConnector(String identifier) {
		return (IInputConnector) getConnectorById(inputConnectors, identifier);
	}
	
	/**
	 * get output connector
	 * @return process output
	 */
	private IOutputConnector getOutputConnector(String identifier) {
		return (IOutputConnector) getConnectorById(outputConnectors, identifier);
	}
	
	/**
	 * get connector by identifier
	 * @param connectors input connectors
	 * @param identifier target identifier
	 * @return connector matching the identifier, null if no such connector exists
	 */
	private IIOConnector getConnectorById(Set<? extends IIOConnector> connectors, String identifier){
		for(IIOConnector connector : connectors){
			if(connector.getIdentifier().equals(identifier))
				return connector;
		}
		return null;
	}

	/**
	 * set start time for this process
	 */
	private void setStartTime() {
		setOutputConnector(OUT_START, new LongLiteral(OUT_START, System.currentTimeMillis(), 
				new MeasurementDescription(
						null,
						"Starttime",
						"Start time of the operation in Unix time",
						RDFVocabulary.LONG.getResource(),
						RDFVocabulary.TIME_INSTANT.getResource(),
						LongLiteral.getMaxRange(), 
						RDFVocabulary.UOM_MILLISECOND.getResource())));
	}
	
	/**
	 * get start time of the last process
	 * @return latest process start time
	 */
	public LongLiteral getStartTime(){
		return (LongLiteral) getOutputConnector(OUT_START).getData();
	}
	
	/**
	 * set runtime of the process
	 */
	protected void setRuntime(){
		setOutputConnector(OUT_RUNTIME, new LongLiteral(OUT_RUNTIME, System.currentTimeMillis() - getStartTime().resolve(),
				new MeasurementDescription(
						null,
						"Runtime", 
						"Runtime of the operation in milliseconds",
						RDFVocabulary.LONG.getResource(),
						RDFVocabulary.TIME_INTERVAL.getResource(),
						LongLiteral.getPositiveRange(), 
						RDFVocabulary.UOM_MILLISECOND.getResource())));
	}

	/**
	 * validate output connectors
	 */
	private void validateOutputs() {
		for(IOutputConnector outputConnector : getOutputConnectors()){
			outputConnector.validate();
		}
	}
	
	/**
	 * get operation outputs
	 * @return operation outputs
	 */
	private Map<String,IData> getOutputs() {
		Map<String,IData> outputs = new HashMap<String,IData>();
		for(IOutputConnector outputConnector : outputConnectors){
			outputs.put(outputConnector.getIdentifier(), outputConnector.getData());
		}
		return outputs;
	}

	/**
	 * execution of the process
	 */
	public abstract void execute();
	
	@Override
	public IOperationDescription getDescription() {
		return new OperationDescription(
				getIdentifier(),
				getInputConnectors(), 
				getOutputConnectors(), 
				getProcessDescription());
	}	
	
	/**
	 * get process description
	 * @return process description
	 */
	public IProcessDescription getProcessDescription() {
		return new ProcessDescription(
				getIdentifier(),
				getProcessTitle(), 
				getProcessAbstract());
	}
	
	/**
	 * get process title
	 * @return process title
	 */
	public abstract String getProcessTitle();
	
	/**
	 * get process description
	 * @return process description
	 */
	public abstract String getProcessAbstract();

}
