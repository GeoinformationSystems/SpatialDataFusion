package de.tud.fusion.data.description;

/**
 * Basic description object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IDescription {
	
	/**
	 * get title (http://purl.org/dc/terms/title)
	 * @return title
	 */
	public String getTitle();
	
	/**
	 * get abstract description (http://purl.org/dc/terms/description)
	 * @return abstract description
	 */
	public String getDescription();

}
