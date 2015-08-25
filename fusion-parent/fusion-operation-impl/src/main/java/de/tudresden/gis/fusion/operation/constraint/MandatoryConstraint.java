package de.tudresden.gis.fusion.operation.constraint;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;

public class MandatoryConstraint implements IDataConstraint {
	
	private String[] keys;
	
	public MandatoryConstraint(String[] keys){
		this.keys = keys;
	}
	
	public MandatoryConstraint(String key){
		this(new String[]{key});
	}

	@Override
	public boolean compliantWith(Map<String, IData> data) {
		for(String key : keys){
			if(!data.containsKey(key) || data.get(key) == null)
				return false;
		}
		return true;
	}

}
