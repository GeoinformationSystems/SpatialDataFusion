package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.description.IResourceDescription;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;

public interface IProcessDescription extends IResourceDescription {

	/**
	 * get operation process constraints
	 * @return process constraints
	 */
	public Collection<IProcessConstraint> constraints();
	
}
