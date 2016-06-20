package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public interface IIODataDescription extends IDataDescription {

	/**
	 * get operation IO constraints
	 * @return IO constraints
	 */
	public Collection<IDataConstraint> constraints();
	
}
