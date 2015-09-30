package de.tudresden.gis.fusion.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOperationProfile;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;
import de.tudresden.gis.fusion.operation.description.OperationProfile;
import de.tudresden.gis.fusion.operation.description.ProcessDescription;

public abstract class AOperationInstance implements IOperationInstance {
	
	public static final String OUT_START = "OUT_START";
	public static final String OUT_RUNTIME = "OUT_RUNTIME";

	private Map<String,IData> input = new HashMap<String,IData>();
	private Map<String,IData> output = new HashMap<String,IData>();
	
	public AOperationInstance(){
		
	}

	@Override
	public Map<String,IData> execute(Map<String,IData> input) {
		//clear outputs
		clearOutput();
		//set inputs
		setInput(input);
		//set start time
		setStart();
		//execute
		validate();
		execute();
		setRuntime();
		//return result
		return output();
	}
	
	private void validate() throws ProcessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IOperationProfile profile() {
		return new OperationProfile(
				getProcessIdentifier(),
				getInputDescription(), 
				getOutputDescriptions(), 
				getProcessDescription());
	}
	
	/**
	 * get unique process identifier
	 * @return process identifier
	 */
	public abstract String getProcessIdentifier();
	
	/**
	 * abstract method: get input descriptions
	 * @return input description
	 */
	public abstract Map<String, IInputDescription> getInputDescription();

	/**
	 * abstract method: get output descriptions
	 * @return output descriptions
	 */
	public abstract Map<String, IOutputDescription> getOutputDescriptions();

	/**
	 * execution of the process (called by execute(Map<String, IData> input))
	 */
	public abstract void execute() throws ProcessException;
	
	/**
	 * clear input map
	 */
	public void clearInput(){
		if(input == null)
			input = new HashMap<String,IData>();
		else
			input.clear();
	}
	
	/**
	 * clear output map
	 */
	protected void clearOutput(){
		if(output == null)
			output = new HashMap<String,IData>();
		else
			output.clear();
	}

	/**
	 * get all inputs
	 * @return inputs
	 */
	public Map<String,IData> input() {
		return input;
	}
	
	/**
	 * get input by key
	 * @param key input key
	 * @return input for specified key
	 */
	public IData input(String key) {
		return input.get(key);
	}

	/**
	 * set input map
	 * @param input input map
	 */
	public void setInput(Map<String,IData> input) {
		this.input = input;
	}
	
	/**
	 * set input
	 * @param key input key
	 * @param value input value
	 */
	public void setInput(String key, IData value) {
		input.put(key, value);
	}

	/**
	 * check if input contains the specified key
	 * @param key key
	 * @return true if input contains the specified key
	 */
	public boolean inputContainsKey(String key){
		return input.containsKey(key);
	}
	
	/**
	 * get output map
	 * @return output map
	 */
	public Map<String,IData> output() {
		return output;
	}
	
	/**
	 * get output by key
	 * @param key output key
	 * @return output for specified key
	 */
	public IData output(String key) {
		return output.get(key);
	}

	/**
	 * set output map
	 * @param output output map
	 */
	public void setOutput(Map<String,IData> output) {
		this.output = output;
	}
	
	/**
	 * set output
	 * @param key output key
	 * @param value output value
	 */
	public void setOutput(String key, IData value) {
		output.put(key, value);
	}
	
	/**
	 * check if output contains the specified key
	 * @param key key
	 * @return true if output contains the specified key
	 */
	public boolean outputContainsKey(String key){
		return output.containsKey(key);
	}
	
	/**
	 * set process start time
	 */
	protected void setStart(){
		setOutput(OUT_START, new LongLiteral(System.currentTimeMillis(), 
				new MeasurementDescription(
						RDFVocabulary.MEASURMENT_TIME_INSTANT.asString(),
						"Start", 
						"Start time of the operation in Unix time", 
						LongLiteral.maxRange(), 
						RDFVocabulary.UOM_MILLISECOND.asResource())));
	}
	
	/**
	 * get latest process start time
	 * @return latest process start time
	 */
	public LongLiteral start(){
		return (LongLiteral) output(OUT_START);
	}

	/**
	 * set runtime of the process
	 */
	protected void setRuntime(){
		setOutput(OUT_RUNTIME, new LongLiteral(System.currentTimeMillis() - start().resolve(),
				new MeasurementDescription(
						RDFVocabulary.MEASUREMENT_TIME_INTERVAL.asString(),
						"Runtime", 
						"Runtime of the operation",  
						LongLiteral.positiveRange(), 
						RDFVocabulary.UOM_MILLISECOND.asResource())));
	}
	
	/**
	 * get latest runtime of the process
	 * @return latest runtime of the process
	 */
	public LongLiteral runtime(){
		return (LongLiteral) output(OUT_RUNTIME);
	}
	
	/**
	 * get process description
	 * @return process description
	 */
	public IProcessDescription getProcessDescription() {
		return new ProcessDescription(
				getProcessIdentifier(),
				getProcessTitle(), 
				getTextualProcessDescription(), 
				getProcessConstraints());
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
	public abstract String getTextualProcessDescription();
	
	/**
	 * get process constraints
	 * @return process constraints
	 */
	public abstract Collection<IProcessConstraint> getProcessConstraints();

}
