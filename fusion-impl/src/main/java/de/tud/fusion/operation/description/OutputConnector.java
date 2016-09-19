package de.tud.fusion.operation.description;

import java.util.Set;

/**
 * Output connector implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OutputConnector extends IOConnector implements IOutputConnector {

	/**
	 * constructor
	 * @param description connector description
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 */
	public OutputConnector(String identifier, String title, String description, Set<IDataConstraint> dataConstraints, Set<IDescriptionConstraint> descriptionConstraints) {
		super(identifier, title, description, dataConstraints, descriptionConstraints);
	}
	
	/**
	 * constructor
	 * @param description connector description
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 */
	public OutputConnector(String identifier, String title, String description, IDataConstraint[] dataConstraints, IDescriptionConstraint[] descriptionConstraints) {
		super(identifier, title, description, dataConstraints, descriptionConstraints);
	}

}
