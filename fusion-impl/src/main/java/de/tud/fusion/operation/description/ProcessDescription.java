package de.tud.fusion.operation.description;

import de.tud.fusion.data.description.Description;

/**
 * Process description implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class ProcessDescription extends Description implements IProcessDescription {

	/**
	 * constructor
	 * @param identifier description identifier
	 * @param title data title
	 * @param description data description
	 */
	public ProcessDescription(String identifier, String title, String description) {
		super(identifier, title, description);
	}

}
