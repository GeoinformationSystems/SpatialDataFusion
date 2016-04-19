package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;

public interface IProcessDescription extends IResource {
	
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

	/**
	 * get operation process constraints
	 * @return process constraints
	 */
	public Collection<IProcessConstraint> constraints();
	
}
