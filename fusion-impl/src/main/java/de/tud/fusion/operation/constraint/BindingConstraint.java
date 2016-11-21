package de.tud.fusion.operation.constraint;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tud.fusion.data.IData;
import de.tud.fusion.operation.description.IDataConstraint;

/**
 * Data binding constraint
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class BindingConstraint implements IDataConstraint {
	
	private Set<Class<?>> supportedbindings;
	
	/**
	 * constructor
	 * @param bindings supported bindings
	 */
	public BindingConstraint(Set<Class<?>> supportedbindings){
		this.supportedbindings = supportedbindings;
	}
	
	/**
	 * constructor
	 * @param bindings supported bindings
	 */
	public BindingConstraint(Class<?>[] supportedbindings){
		this(Sets.newHashSet(supportedbindings));
	}
	
	/**
	 * constructor
	 * @param bindings supported bindings
	 * @param isInput flag: true if input binding
	 */
	public BindingConstraint(Class<?> binding){
		this(Sets.newHashSet(binding));
	}

	@Override
	public boolean compliantWith(IData target){
		return compliantWith(target.getClass());
	}
	
	/**
	 * check if object classes are compliant
	 * @param target target object
	 * @return true, if there is a match between the bindings
	 */
	private boolean compliantWith(Class<?> targetClass){
		for(Class<?> binding : supportedbindings){
			if(binding.isAssignableFrom(targetClass))
				return true;
		}
		return false;
	}

}
