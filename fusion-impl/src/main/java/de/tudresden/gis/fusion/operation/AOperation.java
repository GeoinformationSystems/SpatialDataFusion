package de.tudresden.gis.fusion.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.operation.IOperationProfile;
import de.tudresden.gis.fusion.metadata.operation.OperationProfile;
import de.tudresden.gis.fusion.operation.IOperation;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;

/**
 * abstract fusion process
 */
public abstract class AOperation implements IOperation {
	
	private final String OUT_RUNTIME = "OUT_RUNTIME";
	private final String OUT_START_TIME = "OUT_START_TIME";
	
	private Map<String,IData> input = new HashMap<String,IData>();
	private Map<String,IData> output = new HashMap<String,IData>();
	
	protected IOperationProfile profile;
	
	public void clearInput(){
		if(input != null)
			input.clear();
	}
	
	protected void clearOutput(){
		if(output != null)
			output.clear();
	}
	
	public void setInput(String key, IData value){
		input.put(key, value);
	}
	
	public void setInputs(Map<String,IData> inputs){
		this.input = inputs;
	}
	
	protected void setOutput(String key, IData value){
		output.put(key, value);
	}
	
	public IData getInput(String key){
		//return input, if set
		if(input.containsKey(key))
			return input.get(key);
		//return default (returns null if default is not set)
		IIODescription inputDesc = this.getInputDescription(key);
		if(inputDesc == null)
			throw new RuntimeException("no input description set for " + key);
		return inputDesc.getDefault();
	}
	
	public IData getOutput(String key){
		return output.get(key);
	}
	
	public Map<String,IData> getOutputs(){
		return output;
	}
	
	public boolean inputContainsKey(String key){
		return input.containsKey(key);
	}
	
	protected void setRuntime(){
		setOutput(OUT_RUNTIME, new LongLiteral(System.currentTimeMillis() - getLatest_StartTime().getValue()));
	}
	
	public LongLiteral getLatest_Runtime(){
		return (LongLiteral) this.getOutput(OUT_RUNTIME);
	}
	
	protected void setStartTime(){
		setOutput(OUT_START_TIME, new LongLiteral(System.currentTimeMillis()));
	}
	
	public LongLiteral getLatest_StartTime(){
		return (LongLiteral) this.getOutput(OUT_START_TIME);
	}
	
	public Map<String,IData> execute(Map<String,IData> input) throws ProcessException {
		//clear outputs
		clearOutput();
		//set inputs
		setInputs(input);
		setStartTime();
		//check
		if(!isExecutable())
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT);
		//execute
		execute();
		setRuntime();
		//return result
		return getOutputs();
	}
	
	protected abstract void execute();
	
	public boolean isExecutable() {
		//TODO: implement generic validation based on process description, provide details if not executable
		return true;
	}
	
	public IOperationProfile getProfile(){
		if(profile == null)
			setProfile(new OperationProfile(
					getResource().getIdentifier(),
					new HashSet<IIdentifiableResource>(Arrays.asList(getClassification())),
					getProcessTitle(),
					getProcessDescription(),
					new HashSet<IIODescription>(Arrays.asList(getInputDescriptions())),
					new HashSet<IIODescription>(Arrays.asList(getOutputDescriptions()))
			));
		return profile;
	}
	
	protected void setProfile(IOperationProfile profile) {
		this.profile = profile;
	}
	
	protected abstract IIdentifiableResource getResource();
	protected abstract IIdentifiableResource[] getClassification();
	protected abstract String getProcessTitle();
	protected abstract String getProcessDescription();
	protected abstract IIODescription[] getInputDescriptions();
	protected abstract IIODescription[] getOutputDescriptions();
	
	public IIODescription getInputDescription(String key){
		return(getIODescription(getInputDescriptions(), key));
	}
	
	public IIODescription getOutputDescription(String key){
		return(getIODescription(getOutputDescriptions(), key));
	}
	
	public IIODescription getIODescription(IIODescription[] descriptions, String key){
		for(IIODescription desc : descriptions){
			if(desc.getIdentifier().equals(key))
				return desc;
		}
		return null;
	}

}
