package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.IResource;

public interface IDataDescription extends IResource {
	
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