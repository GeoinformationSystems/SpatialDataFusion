package de.tud.fusion.operation.constraint;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.operation.description.IDescriptionConstraint;

/**
 * Data binding constraint
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class DataTypeConstraint implements IDescriptionConstraint {
	
	private Set<IResource> supportedDataTypes;
	
	/**
	 * constructor
	 * @param bindings supported data types
	 */
	public DataTypeConstraint(Set<IResource> supportedDataTypes){
		this.supportedDataTypes = supportedDataTypes;
	}
	
	/**
	 * constructor
	 * @param bindings supported data types
	 */
	public DataTypeConstraint(IResource[] supportedDataTypes){
		this(Sets.newHashSet(supportedDataTypes));
	}
	
	/**
	 * constructor
	 * @param bindings supported data type
	 */
	public DataTypeConstraint(IResource supportedDataType){
		this(Sets.newHashSet(supportedDataType));
	}

	@Override
	public boolean compliantWith(IDataDescription target){
		return compliantWith(target.getDataType());
	}
	
	/**
	 * check if object classes are compliant
	 * @param target target object
	 * @return true, if there is a match between the bindings
	 */
	private boolean compliantWith(IResource target){
		for(IResource dataType : supportedDataTypes){
			if(dataType.getIdentifier().equalsIgnoreCase(target.getIdentifier()))
				return true;
		}
		return false;
	}

}
