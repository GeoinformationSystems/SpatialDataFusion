package de.tudresden.geoinfo.fusion.metadata;

/**
 * Metadata implementation
 */
public class Metadata implements IMetadata {

	/**
	 * data title & description
	 */
	private String title, description;
	
	/**
	 * constructor
	 * @param title data title
	 * @param description data description
	 */
	public Metadata(String title, String description) {
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
