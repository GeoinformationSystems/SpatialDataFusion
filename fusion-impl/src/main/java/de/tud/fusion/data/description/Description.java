package de.tud.fusion.data.description;

import java.net.URI;

import de.tud.fusion.data.rdf.Resource;

/**
 * Description implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Description extends Resource implements IDescription {

	/**
	 * data title & description
	 */
	String title, description;
	
	/**
	 * constructor
	 * @param identifier description identifier
	 * @param title data title
	 * @param description data description
	 */
	public Description(String identifier, String title, String description) {
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

	@Override
	public URI getURI() {
		return null;
	}

	@Override
	public boolean isBlank() {
		return false;
	}

	

}
