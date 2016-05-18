package de.tudresden.gis.fusion.operation.constraint;

import java.util.Collection;
import java.util.HashSet;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class BindingConstraint implements IDataConstraint {
	
	private Collection<Class<?>> bindings;
	
	protected BindingConstraint(Collection<Class<?>> bindings){
		this.bindings = bindings;
	}
	
	protected BindingConstraint(Class<?>[] bindings){
		this.bindings = new HashSet<Class<?>>();
		for(Class<?> binding : bindings){
			this.bindings.add(binding);
		}
	}
	
	protected BindingConstraint(Class<?> binding){
		this.bindings = new HashSet<Class<?>>();
		this.bindings.add(binding);
	}

	@Override
	public boolean compliantWith(IData target){
		return compliantWith(target.getClass());
	}
	
	/**
	 * check if object classes are compliant
	 * @param target target object
	 * @return true, if one of the bindings can be assigned from target class
	 */
	public boolean compliantWith(Class<?> target){
		for(Class<?> binding : bindings){
			if(target.isAssignableFrom(binding))
				return true;
		}
		return false;
	}

}
