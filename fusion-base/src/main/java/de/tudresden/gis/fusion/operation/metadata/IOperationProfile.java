package de.tudresden.gis.fusion.operation.metadata;

import java.util.Collection;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * basic operation description
 * @author Stefan
 *
 */
public interface IOperationProfile extends IRDFTripleSet,IResource {
	
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
	 * get classification for this operation
	 * @return operation classification
	 */
	public Collection<IIdentifiableResource> getClassification();
	
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
