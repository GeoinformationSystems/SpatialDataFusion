package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForConnector;

import java.util.Collection;

public interface IConnector extends IResource {

	/**
	 * connect data object to connector
	 * @param data input data
     * @throws IllegalArgumentException if data object cannot be connected
	 */
	void connect(IData data) throws IllegalArgumentException;

    /**
     * check, if connector has a connected data object
     * @return true, if connector is connected
     */
	boolean isConnected();

    /**
     * check, if connector is valid (e.g. all constraints are fulfilled)
     * @return true, if connector is valid
     */
    boolean isValid();

	/**
	 * get connected data object
	 * @return connected data object or null, if connector is not connected
	 */
	IData getData();

	/**
	 * get connector IO data constraints
	 * @return IO data constraints
	 */
    Collection<IDataConstraint> getDataConstraints();
	
	/**
	 * get connector IO metadata constraints
	 * @return IO metadata constraints
	 */
    Collection<IMetadataConstraint> getMetadataConstraints();

    /**
     * get metadata for connector
     * @return connector metadata
     */
    IMetadataForConnector getMetadata();

}
