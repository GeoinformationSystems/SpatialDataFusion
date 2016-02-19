package de.tudresden.gis.fusion.operation.mapping;

import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class BestCorrespondenceMapping extends AOperationInstance {

	@Override
	public void execute() throws ProcessException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Mapping of Best Correspondences";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Filters a set of feature relations in order to create best correspondences";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IInputDescription> getInputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
