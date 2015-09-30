package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IDataDescription;

public abstract class AbstractData implements IData {
	
	private Object object;
	private IDataDescription description;
	
	public AbstractData(Object object, IDataDescription description){
		this.object = object;
		this.description = description;
	}
	
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
