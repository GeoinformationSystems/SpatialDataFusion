package de.tudresden.geoinfo.fusion.metadata;

/**
 * Basic metadata object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMetadata {
	
	/**
	 * get title (http://purl.org/dc/terms/title)
	 * @return title
	 */
	String getTitle();
	
	/**
	 * get abstract description (http://purl.org/dc/terms/description)
	 * @return abstract description
	 */
	String getDescription();

}
