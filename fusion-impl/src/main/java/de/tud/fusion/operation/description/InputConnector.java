package de.tud.fusion.operation.description;

import java.util.Set;

import de.tud.fusion.data.IData;

/**
 * Input connector implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class InputConnector extends IOConnector implements IInputConnector {
	
	private IData defaultData;

	/**
	 * constructor
	 * @param description connector description
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 */
	public InputConnector(String identifier, String title, String description, Set<IDataConstraint> dataConstraints, Set<IDescriptionConstraint> descriptionConstraints, IData defaultData) {
		super(identifier, title, description, dataConstraints, descriptionConstraints);
		this.defaultData = defaultData;
	}
	
	/**
	 * constructor
	 * @param description connector description
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 */
	public InputConnector(String identifier, String title, String description, IDataConstraint[] dataConstraints, IDescriptionConstraint[] descriptionConstraints, IData defaultData) {
		super(identifier, title, description, dataConstraints, descriptionConstraints);
		this.defaultData = defaultData;
	}

	@Override
	public IData getDefault() {
		return defaultData;
	}
	
	@Override
	public IData getData() {
		return super.getData() != null ? super.getData() : getDefault();
	}

}
