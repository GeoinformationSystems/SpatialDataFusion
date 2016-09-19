package de.tud.fusion.operation.description;

import java.util.Set;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.IIdentifiableObject;
import de.tud.fusion.data.description.IDescription;

public interface IIOConnector extends IIdentifiableObject,IDescription {

	/**
	 * get connector IO data constraints
	 * @return IO data constraints
	 */
	public Set<IDataConstraint> getDataConstraints();
	
	/**
	 * get connector IO description constraints
	 * @return IO description constraints
	 */
	public Set<IDescriptionConstraint> getDescriptionConstraints();
	
	/**
	 * connect data object to connector
	 * @param data input data
	 */
	public void connect(IData data);
	
	/**
	 * get connected data object
	 * @return connected data object
	 */
	public IData getData();
	
	/**
	 * validate IO connection
	 */
	public void validate();
	
}
