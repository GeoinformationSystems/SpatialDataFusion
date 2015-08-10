package de.tudresden.gis.fusion.registry.instance.constraints;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.registry.IObjectDescriptionResource;

public interface IIOConstraint extends IConstraint,IObjectDescriptionResource {

	/**
	 * check whether input data complies with constraint
	 * @param input input data
	 * @return true, if constraint is met by input data object
	 */
	public boolean compliantWith(IData input);
	
}
