package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IDataDescription;

/**
 * abstract data implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractData implements IData {
	
	/**
	 * data object
	 */
	private Object object;
	
	/**
	 * data description
	 */
	private IDataDescription description;
	
	/**
	 * constructor
	 * @param object data object
	 * @param description data description
	 */
	public AbstractData(Object object, IDataDescription description){
		this.object = object;
		this.description = description;
	}
	
	/**
	 * constructor
	 * @param object data object
	 */
	public AbstractData(Object object){
		this(object, null);
	}
	
	/**
	 * set data object
	 * @param object input data object
	 */
	protected void setObject(Object object){
		this.object = object;
	}
	
	/**
	 * set data description
	 * @param description input data description
	 */
	protected void setDescription(IDataDescription description){
		this.description = description;
	}

	@Override
	public Object resolve() {
		return object;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

}
