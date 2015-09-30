package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;

public interface IProcessDescription extends IDataDescription {

	/**
	 * get operation process constraints
	 * @return process constraints
	 */
	public Collection<IProcessConstraint> constraints();
	
}
