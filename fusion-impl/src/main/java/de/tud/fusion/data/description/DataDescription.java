package de.tud.fusion.data.description;

import de.tud.fusion.data.rdf.IResource;

/**
 * Data description implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class DataDescription extends Description implements IDataDescription {
	
	private IResource dataType;
	
	/**
	 * constructor
	 * @param identifier description identifier
	 * @param title data title
	 * @param description data description
	 * @param bindings supported Java bindings
	 */
	public DataDescription(String identifier, String title, String description, IResource dataType) {
		super(identifier, title, description);
		this.dataType = dataType;
	}

	@Override
	public IResource getDataType() {
		return dataType;
	}

}
