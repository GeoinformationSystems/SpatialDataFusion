package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.Resource;

/**
 * data description implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class DataDescription extends Resource implements IDataDescription {

	/**
	 * data title & description
	 */
	String title, description;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param title data title
	 * @param description data description
	 */
	public DataDescription(String identifier, String title, String description){
		super(identifier);
		this.title = title;
		this.description = description;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
