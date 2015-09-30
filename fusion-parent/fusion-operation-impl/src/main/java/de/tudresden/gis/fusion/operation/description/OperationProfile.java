package de.tudresden.gis.fusion.operation.description;

import java.util.Map;

import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOperationProfile;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;

public class OperationProfile extends Resource implements IOperationProfile {
	
	private Map<String,IInputDescription> inputDesc;
	private Map<String,IOutputDescription> outputDesc;
	private IProcessDescription processDesc;
	
	public OperationProfile(String identifier, Map<String,IInputDescription> inputDesc, Map<String,IOutputDescription> outputDesc, IProcessDescription processDesc){
		super(identifier);
		this.inputDesc = inputDesc;
		this.outputDesc = outputDesc;
		this.processDesc = processDesc;
	}

	@Override
	public Map<String,IInputDescription> inputDescription() {
		return inputDesc;
	}

	@Override
	public Map<String,IOutputDescription> outputDescription() {
		return outputDesc;
	}

	@Override
	public IProcessDescription processDescription() {
		return processDesc;
	}

}
