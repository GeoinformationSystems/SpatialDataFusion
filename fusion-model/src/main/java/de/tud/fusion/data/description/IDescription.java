package de.tud.fusion.data.description;

import de.tud.fusion.data.rdf.IResource;

/**
 * Basic description object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IDescription extends IResource {
	
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
