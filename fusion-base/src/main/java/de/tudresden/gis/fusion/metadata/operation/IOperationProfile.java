package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IDescription;
import de.tudresden.gis.fusion.metadata.data.IIODescription;

/**
 * basic operation description
 * @author Stefan
 *
 */
public interface IOperationProfile extends IDescription {
	
	/**
	 * get name of the process
	 * @return process name
	 */
	public String getProcessName();
	
	/**
	 * get process description
	 * @return process description
	 */
	public String getProcessDescription();
	
	/**
	 * get classifications for this operation
	 * @return operation classification
	 */
	public Set<IIdentifiableResource> getClassification();
	
	/**
	 * get input descriptions
	 * @return input descriptions
	 */
	public Collection<IIODescription> getInputDescriptions();
	
	/**
	 * get output descriptions
	 * @return output descriptions
	 */
	public Collection<IIODescription> getOutputDescriptions();
	
}
