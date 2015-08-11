package de.tudresden.gis.fusion.operation;

import java.util.Map;

import de.tudresden.gis.fusion.operation.description.IIODataDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;

public abstract class OperationProfile implements IOperationProfile {

	@Override
	public Map<String,IIODataDescription> getInputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String,IIODataDescription> getOutputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcessDescription getProcessDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
