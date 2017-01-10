package de.tudresden.geoinfo.fusion.metadata;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * MeasurementData range implementation
 */
public class MeasurementRange<T extends Comparable<T>> implements IMeasurementRange<T> {

	private SortedSet<T> members;
	private boolean continuous;
	
	/**
	 * constructor
	 * @param members range member
	 * @param continuous flag: continuous range
	 */
	public MeasurementRange(SortedSet<T> members, boolean continuous){
		this.members = members;
		this.continuous = continuous;
	}

	/**
	 * constructor
	 * @param members range member
	 * @param continuous flag: continuous range
	 */
	public MeasurementRange(T[] members, boolean continuous){
		this(new TreeSet<>(Arrays.asList(members)), continuous);
	}

	@Override
	public SortedSet<T> getRangeMembers() {
		return members;
	}

	@Override
	public boolean isContinuous() {
		return continuous;
	}

	@Override
	public T getMin() {
		return members.first();
	}

	@Override
	public T getMax() {
		return members.last();
	}

	@Override
	public boolean contains(T target) {
		if(!isContinuous())
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
		for(T value : getRangeMembers()){
			if(value.compareTo(target) == 0)
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
		return getMin().compareTo(target) >= 0 && getMax().compareTo(target) <= 0;
	}

}
