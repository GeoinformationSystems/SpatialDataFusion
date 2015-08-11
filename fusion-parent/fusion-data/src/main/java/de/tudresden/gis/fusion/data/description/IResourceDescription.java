package de.tudresden.gis.fusion.data.description;

public interface IResourceDescription {

	/**
	 * get title (http://purl.org/dc/terms/title)
	 * @return title
	 */
	public String getTitle();
	
	/**
	 * get abstract (http://purl.org/dc/terms/abstract)
	 * @return abstract
	 */
	public String getAbstract();
	
}
