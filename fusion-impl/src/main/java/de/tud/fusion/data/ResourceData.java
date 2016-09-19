package de.tud.fusion.data;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.Resource;

/**
 * Data object implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class ResourceData extends Resource implements IData {
	
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
	 * @param identifier data object identifier
	 * @param object data object
	 * @param description data object description
	 */
	public ResourceData(String identifier, Object object, IDataDescription description){
		super(identifier);
		this.object = object;
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

	/**
	 * set data object
	 * @param object input data object
	 */
	protected void setObject(Object object){
		this.object = object;
	}

}
