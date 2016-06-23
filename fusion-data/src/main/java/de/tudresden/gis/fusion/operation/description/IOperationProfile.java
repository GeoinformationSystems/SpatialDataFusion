package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;
import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * basic operation profile
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IOperationProfile extends IResource {

	/**
	 * get input descriptions
	 * @return operation input descriptions
	 */
	public Collection<IInputDescription> inputDescriptions();
	
	/**
	 * get output descriptions
	 * @return operation output descriptions
	 */
	public Collection<IOutputDescription> outputDescriptions();
	
	/**
	 * get process description for this operation
	 * @return process description
	 */
	public IProcessDescription processDescription();
	
}
