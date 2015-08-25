package de.tudresden.gis.fusion.operation.constraint;

import java.util.HashMap;
import java.util.Map;
import de.tudresden.gis.fusion.data.IData;

public class BindingConstraint implements IDataConstraint {
	
	private Map<String,Class<? extends IData>[]> bindings;
	
	public BindingConstraint(Map<String,Class<? extends IData>[]> bindings){
		this.bindings = bindings;
	}
	
	public BindingConstraint(String key, Class<? extends IData>[] bindings){
		this.bindings = new HashMap<String,Class<? extends IData>[]>();
		this.bindings.put(key, bindings);
	}
	
	@SuppressWarnings("unchecked")
	public BindingConstraint(String key, Class<? extends IData> binding){
		this.bindings = new HashMap<String,Class<? extends IData>[]>();
		this.bindings.put(key, new Class[]{binding});
	}

	@Override
	public boolean compliantWith(Map<String,IData> data) {
		for(String key : bindings.keySet()){
			if(!data.containsKey(key) || !compliantWith(bindings.get(key), data.get(key).getClass()))
				return false;
		}
		return true;
	}
	
	private boolean compliantWith(Class<? extends IData>[] bindings, Class<? extends IData> target){
		for(Class<? extends IData> binding : bindings){
			if(target.isAssignableFrom(binding))
				return true;
		}
		return false;
	}

}
