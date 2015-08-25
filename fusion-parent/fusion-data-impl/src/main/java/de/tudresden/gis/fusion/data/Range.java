package de.tudresden.gis.fusion.data;

import java.util.Arrays;

public class Range<T extends Comparable<T>> implements IRange<T> {

	private T[] elements;
	private boolean continuous;
	
	public Range(T[] elements, boolean continuous){
		this.elements = elements;
		Arrays.sort(elements);
		this.continuous = continuous;
	}
	
	@Override
	public T[] valueRange() {
		return elements;
	}

	@Override
	public boolean continuous() {
		return continuous;
	}

	@Override
	public T min() {
		return elements[0];
	}

	@Override
	public T max() {
		return elements[elements.length-1];
	}

	@Override
	public boolean contains(T target) {
		if(!continuous())
			return partOfRange(target);
		else
			return inBetweenRange(target);
	}
	
	/**
	 * check if value is part of element range
	 * @param target target value
	 * @return true, if target value is member of range
	 */
	private boolean partOfRange(T target){
		for(T value : elements){
			if(value.equals(target))
				return true;
		}
		return false;
	}
	
	/**
	 * check if value is between min and max
	 * @param target target value
	 * @return true, if min < value < max
	 */
	private boolean inBetweenRange(T target){
		return(min().compareTo(target) >= 0 && max().compareTo(target) <= 0);
	}

}
