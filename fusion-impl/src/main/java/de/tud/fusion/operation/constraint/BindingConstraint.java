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
	private boolean isInput;
	
	/**
	 * constructor
	 * @param bindings supported bindings
	 * @param isInput flag: true if input binding
	 */
	public BindingConstraint(Set<Class<?>> supportedbindings, boolean isInput){
		this.supportedbindings = supportedbindings;
		this.isInput = isInput;
	}
	
	/**
	 * constructor
	 * @param bindings supported bindings
	 * @param isInput flag: true if input binding
	 */
	public BindingConstraint(Class<?>[] supportedbindings, boolean isInput){
		this(Sets.newHashSet(supportedbindings), isInput);
	}
	
	/**
	 * constructor
	 * @param bindings supported bindings
	 * @param isInput flag: true if input binding
	 */
	public BindingConstraint(Class<?> binding, boolean isInput){
		this(Sets.newHashSet(binding), isInput);
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
			if(isInput && binding.isAssignableFrom(targetClass))
				return true;
			else if(!isInput && targetClass.isAssignableFrom(binding))
				return true;
		}
		return false;
	}

}
