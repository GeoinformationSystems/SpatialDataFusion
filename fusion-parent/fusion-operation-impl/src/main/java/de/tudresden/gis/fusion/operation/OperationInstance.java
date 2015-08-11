package de.tudresden.gis.fusion.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.literal.LongBinding;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.operation.constraint.IConstraint;

public abstract class OperationInstance implements IOperationInstance {
	
	public static final String OUT_START = "OUT_START";
	public static final String OUT_RUNTIME = "OUT_RUNTIME";

	private Map<String,IData> input = new HashMap<String,IData>();
	private Map<String,IData> output = new HashMap<String,IData>();
	
	public OperationInstance(){
		
	}

	@Override
	public Map<String, IData> execute(Map<String,IData> input) {
		//clear outputs
		clearOutput();
		//set inputs
		setInput(input);
		//set start time
		setStart();
		//execute
		execute();
		setRuntime();
		//return result
		return getOutput();
	}

	/**
	 * execution of the process (called by execute(Map<String, IData> input))
	 */
	public abstract void execute() throws ProcessException;

	@Override
	public IOperationProfile getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IConstraint> getConstraints() {
		// TODO Auto-generated method stub
		return null;
	}
	
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
	public Map<String,IData> getInput() {
		return input;
	}
	
	/**
	 * get input by key
	 * @param key input key
	 * @return input for specified key
	 */
	public IData getInput(String key) {
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
	 * get output map
	 * @return output map
	 */
	public Map<String,IData> getOutput() {
		return output;
	}
	
	/**
	 * get output by key
	 * @param key output key
	 * @return output for specified key
	 */
	public IData getOutput(String key) {
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
	 * set process start time
	 */
	protected void setStart(){
		setOutput(OUT_START, new LongBinding(System.currentTimeMillis(), RDFVocabulary.UOM_MILLISECOND.resource()));
	}
	
	/**
	 * get latest process start time
	 * @return latest process start time
	 */
	public LongBinding getStart(){
		return (LongBinding) getOutput(OUT_START);
	}

	/**
	 * set runtime of the process
	 */
	protected void setRuntime(){
		setOutput(OUT_RUNTIME, new LongBinding(System.currentTimeMillis() - getStart().getValue(), RDFVocabulary.UOM_MILLISECOND.resource()));
	}
	
	/**
	 * get latest runtime of the process
	 * @return latest runtime of the process
	 */
	public LongBinding getRuntime(){
		return (LongBinding) getOutput(OUT_RUNTIME);
	}

}
