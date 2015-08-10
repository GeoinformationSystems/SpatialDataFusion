package de.tudresden.gis.fusion.registry.model;

import de.tudresden.gis.fusion.registry.IObjectDescriptionResource;

public interface IOperationProfile extends IObjectDescriptionResource {
	
	/**
	 * get input description for this operation
	 * @return input description
	 */
	public IInputDescription getInputDescription();
	
	/**
	 * get output description for this operation
	 * @return output description
	 */
	public IOutputDescription getOutputDescription();
	
	/**
	 * get description of process of this operation
	 * @return process description
	 */
	public IProcessDescription getProcessDescription();
	
}
