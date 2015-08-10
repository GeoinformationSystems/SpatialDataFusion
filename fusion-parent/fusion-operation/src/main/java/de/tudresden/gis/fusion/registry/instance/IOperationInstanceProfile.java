package de.tudresden.gis.fusion.registry.instance;

import java.util.Set;

import de.tudresden.gis.fusion.registry.IObjectDescription;
import de.tudresden.gis.fusion.registry.instance.constraints.IInputConstraint;
import de.tudresden.gis.fusion.registry.instance.constraints.IOutputConstraint;
import de.tudresden.gis.fusion.registry.instance.constraints.IProcessConstraint;
import de.tudresden.gis.fusion.registry.model.IOperationProfile;

public interface IOperationInstanceProfile extends IObjectDescription {

	/**
	 * get generic profile implemented by the instance
	 * @return operation profile
	 */
	public IOperationProfile getImplementedProfile();
	
	/**
	 * get input constraints for this operation
	 * @return input constraints
	 */
	public Set<IInputConstraint> getInputConstraints();
	
	/**
	 * get output constraints for this operation
	 * @return output constraints
	 */
	public Set<IOutputConstraint> getOutputConstraints();
	
	/**
	 * get description of process constraints of this operation
	 * @return process description
	 */
	public Set<IProcessConstraint> getProcessConstraints();
	
}
