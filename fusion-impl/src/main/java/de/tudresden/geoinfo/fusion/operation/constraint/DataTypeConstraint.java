package de.tudresden.geoinfo.fusion.operation.constraint;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.metadata.IMetadata;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import de.tudresden.geoinfo.fusion.operation.IMetadataConstraint;

import java.util.Set;

/**
 * Data binding constraint
 */
public class DataTypeConstraint implements IMetadataConstraint {
	
	private Set<IResource> supportedDataTypes;
	
	/**
	 * constructor
	 * @param supportedDataTypes supported data types
	 */
	public DataTypeConstraint(Set<IResource> supportedDataTypes){
		this.supportedDataTypes = supportedDataTypes;
	}
	
	/**
	 * constructor
	 * @param supportedDataTypes supported data types
	 */
	public DataTypeConstraint(IResource[] supportedDataTypes){
		this(Sets.newHashSet(supportedDataTypes));
	}
	
	/**
	 * constructor
	 * @param supportedDataType supported data type
	 */
	public DataTypeConstraint(IResource supportedDataType){
		this(Sets.newHashSet(supportedDataType));
	}

	@Override
	public boolean compliantWith(IMetadata target){
		if(target instanceof IMetadataForData)
		    return compliantWith(((IMetadataForData) target).getDataType());
		return false;
	}
	
	/**
	 * check if object classes are compliant
	 * @param target target object
	 * @return true, if there is a match between the bindings
	 */
	private boolean compliantWith(IResource target){
		for(IResource dataType : supportedDataTypes){
			if(dataType.getIdentifier().equals(target.getIdentifier()))
				return true;
		}
		return false;
	}

}
