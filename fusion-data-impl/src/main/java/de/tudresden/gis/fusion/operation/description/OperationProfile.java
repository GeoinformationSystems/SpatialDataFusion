package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOperationProfile;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;

public class OperationProfile extends Resource implements IOperationProfile {
	
	private Collection<IInputDescription> inputDesc;
	private Collection<IOutputDescription> outputDesc;
	private IProcessDescription processDesc;
	
	public OperationProfile(String identifier, Collection<IInputDescription> inputDesc, Collection<IOutputDescription> outputDesc, IProcessDescription processDesc){
		super(identifier);
		this.inputDesc = inputDesc;
		this.outputDesc = outputDesc;
		this.processDesc = processDesc;
	}

	@Override
	public Collection<IInputDescription> inputDescriptions() {
		return inputDesc;
	}

	@Override
	public Collection<IOutputDescription> outputDescriptions() {
		return outputDesc;
	}

	@Override
	public IProcessDescription processDescription() {
		return processDesc;
	}

}
