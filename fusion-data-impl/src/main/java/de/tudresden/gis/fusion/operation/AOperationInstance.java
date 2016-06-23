package de.tudresden.gis.fusion.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.BindingConstraint;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.constraint.MandatoryConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOperationProfile;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;
import de.tudresden.gis.fusion.operation.description.OperationProfile;
import de.tudresden.gis.fusion.operation.description.ProcessDescription;

public abstract class AOperationInstance implements IOperation {
	
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
				getInputDescriptions(), 
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
	public abstract Collection<IInputDescription> getInputDescriptions();
	
	/**
	 * get input data
	 * @param identifier input identifier
	 * @return input data object
	 * @throws IllegalArgumentException if identifier was not found
	 */
	public IData getInput(String identifier){
		if(!inputContainsKey(identifier)){
			return getDefaultInput(identifier);
		}
		validateInput(input(identifier), getInputDescription(identifier));
		return input(identifier);
	}
	
	/**
	 * validate input data
	 * @param input input data
	 * @param inputDescription process input data description
	 * @throws ProcessException if input does not match input description
	 */
	private void validateInput(IData input, IInputDescription inputDescription) {
		for(IDataConstraint constraint : inputDescription.constraints()){
			if(constraint instanceof BindingConstraint && !((BindingConstraint) constraint).compliantWith(input))
				throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "input does not match required binding");
		}
	}

	/**
	 * get input description for identifier
	 * @param identifier input identifier
	 * @return input description
	 * @throws IllegalArgumentException if identifier was not found
	 */
	public IInputDescription getInputDescription(String identifier){	
		for(IInputDescription inputDesc : getInputDescriptions()){
			if(inputDesc.getIdentifier().equalsIgnoreCase(identifier)){
				return inputDesc;
			}
		}
		throw new IllegalArgumentException("Identifier " + identifier + " has no description");
	}
	
	/**
	 * check, if input data is mandatory
	 * @param identifier input identifier
	 * @return true, if input data is mandatory, false otherwise
	 */
	public boolean isMandatory(String identifier) {
		IInputDescription desc = getInputDescription(identifier);
		for(IDataConstraint constraint : desc.constraints()){
			if(constraint instanceof MandatoryConstraint)
				return true;
		}
		return false;
	}
	
	/**
	 * get default input for identifier
	 * @param identifier input identifier
	 * @return default input
	 * @throws IllegalArgumentException if identifier was not found or has no default
	 */
	public IData getDefaultInput(String identifier){
		IInputDescription desc = getInputDescription(identifier);
		if(desc.getDefault() != null)
			return desc.getDefault();
		else {
			if(isMandatory(identifier))
				throw new ProcessException(ExceptionKey.INPUT_MISSING, "Identifier " + identifier + " has no default");
			else
				return null;
		}
	}

	/**
	 * abstract method: get output descriptions
	 * @return output descriptions
	 */
	public abstract Collection<IOutputDescription> getOutputDescriptions();

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
	private void clearOutput(){
		if(output == null)
			output = new HashMap<String,IData>();
		else
			output.clear();
	}
	
	/**
	 * get input by key
	 * @param key input key
	 * @return input for specified key
	 */
	private IData input(String key) {
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
						RDFVocabulary.TIME_INSTANT.getString(),
						"Start", 
						"Start time of the operation in Unix time", 
						LongLiteral.maxRange(), 
						RDFVocabulary.UOM_MILLISECOND.getResource())));
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
						RDFVocabulary.TIME_INTERVAL.getString(),
						"Runtime", 
						"Runtime of the operation",  
						LongLiteral.positiveRange(), 
						RDFVocabulary.UOM_MILLISECOND.getResource())));
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
