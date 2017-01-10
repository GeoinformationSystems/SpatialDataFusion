package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForConnector;

import java.util.Set;

/**
 * Output connector implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OutputConnector extends Connector implements IOutputConnector {

	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 */
	public OutputConnector(IIdentifier identifier, IMetadataForConnector metadata, Set<IDataConstraint> dataConstraints, Set<IMetadataConstraint> descriptionConstraints) {
		super(identifier, metadata, dataConstraints, descriptionConstraints);
	}
	
	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 */
	public OutputConnector(IIdentifier identifier, IMetadataForConnector metadata, IDataConstraint[] dataConstraints, IMetadataConstraint[] descriptionConstraints) {
		super(identifier, metadata, dataConstraints, descriptionConstraints);
	}

}
