package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForOperation;

import java.util.Map;

/**
 * Basic operation object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IOperation extends IResource {

	/**
	 * executes an operation
	 * @param input input parameters used to execute the operation
	 * @return operation output
	 */
    Map<IIdentifier,IData> execute(Map<IIdentifier,IData> input);

	/**
	 * get input connector by id
	 * @param id connector identifier
	 * @return input connector or null, if no such connector exist
	 */
	IInputConnector getInputConnector(IIdentifier id);

	/**
	 * get output connector by id
	 * @param id connector identifier
	 * @return input connector or null, if no such connector exist
	 */
	IOutputConnector getOutputConnector(IIdentifier id);
	
	/**
	 * returns operation metadata
	 * @return operation metadata
	 */
    IMetadataForOperation getMetadata();
	
}
