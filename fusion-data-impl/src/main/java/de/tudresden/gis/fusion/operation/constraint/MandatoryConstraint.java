package de.tudresden.gis.fusion.operation.constraint;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class MandatoryConstraint implements IDataConstraint {
	
	private String key;
	
	protected MandatoryConstraint(String key){
		this.key = key;
	}

	public boolean compliantWith(Map<String,IData> data) {
		if(data.containsKey(key) && compliantWith(data.get(key)))
			return true;
		return false;
	}

	@Override
	public boolean compliantWith(IData data) {
		return data != null;
	}

}
