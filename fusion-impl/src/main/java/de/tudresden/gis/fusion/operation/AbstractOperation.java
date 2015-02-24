package de.tudresden.gis.fusion.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.metadata.OperationProfile;
import de.tudresden.gis.fusion.operation.IOperation;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;
import de.tudresden.gis.fusion.operation.metadata.IOperationProfile;

/**
 * abstract fusion process
 */
public abstract class AbstractOperation implements IOperation {
	
	private final String OUT_RUNTIME = "OUT_RUNTIME";
	private final String OUT_START_TIME = "OUT_START_TIME";
	
	private Map<String,IData> input;
	private Map<String,IData> output;
	
	private IOperationProfile profile;

	public AbstractOperation(){
		initDescription();
		initInput();
		initOutput();
	}
	
	private void initInput(){
		if(input == null)
			input = new HashMap<String,IData>();
	}
	
	private void initOutput(){
		if(output == null)
			output = new HashMap<String,IData>();
	}
	
	public void clearInput(){
		if(input != null)
			input.clear();
	}
	
	protected void clearOutput(){
		if(output != null)
			output.clear();
	}
	
	public void setInput(String key, IData value){
		initInput();
		input.put(key, value);
	}
	
	public void setInputs(Map<String,IData> inputs){
		this.input = inputs;
	}
	
	protected void setOutput(String key, IData value){
		initOutput();
		output.put(key, value);
	}
	
	public IData getInput(String key){
		//return input, if set
		if(input.containsKey(key))
			return input.get(key);
		//return default (returns null if default is not set)
		IIODescription inputDesc = this.getInputDescription(new IRI(key));
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
		return profile;
	}
	
	protected void setDescription(IOperationProfile profile){
		this.profile = profile;
	}
	
	protected void initDescription(){
		setDescription(new OperationProfile(
				getProcessIRI(),
				getProcessTitle(),
				getProcessDescription(),
				getInputDescriptions(),
				getOutputDescriptions()
		));
	}
	
	protected abstract IIRI getProcessIRI();
	protected abstract String getProcessTitle();
	protected abstract String getProcessDescription();
	protected abstract Collection<IIODescription> getInputDescriptions();
	protected abstract Collection<IIODescription> getOutputDescriptions();
	
	public IIODescription getInputDescription(IIRI key){
		return(getIODescription(getInputDescriptions(), key));
	}
	
	public IIODescription getOutputDescription(IIRI key){
		return(getIODescription(getOutputDescriptions(), key));
	}
	
	public IIODescription getIODescription(Collection<IIODescription> descriptions, IIRI key){
		for(IIODescription desc : descriptions){
			if(desc.getIdentifier().equals(key))
				return desc;
		}
		return null;
	}

}
