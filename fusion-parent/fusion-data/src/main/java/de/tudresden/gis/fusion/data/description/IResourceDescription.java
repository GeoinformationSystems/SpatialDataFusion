package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.IRDFResource;

public interface IResourceDescription extends IRDFResource {

	/**
	 * get title (http://purl.org/dc/terms/title)
	 * @return title
	 */
	public String title();
	
	/**
	 * get abstract (http://purl.org/dc/terms/abstract)
	 * @return abstract
	 */
	public String abstrakt();
	
}
