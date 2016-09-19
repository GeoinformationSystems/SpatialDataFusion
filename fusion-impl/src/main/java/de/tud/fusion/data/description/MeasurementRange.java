package de.tud.fusion.data.description;

import java.util.Arrays;

import de.tud.fusion.data.IMeasurement;
import de.tud.fusion.data.literal.Measurement;

/**
 * Measurement range implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class MeasurementRange<T extends Comparable<T>> implements IMeasurementRange {
	
	private Measurement<T>[] members;
	private boolean continuous;
	
	/**
	 * constructor
	 * @param members range member
	 * @param continuous flag: continuous range
	 */
	public MeasurementRange(Measurement<T>[] members, boolean continuous){
		this.members = members;
		sort();
		this.continuous = continuous;
	}

	/**
	 * sort member range
	 */
	private void sort() {
		Arrays.sort(members);
	}

	@Override
	public Measurement<T>[] getMembers() {
		return members;
	}

	@Override
	public boolean isContinuous() {
		return continuous;
	}

	@Override
	public Measurement<T> getMin() {
		return members[0];
	}

	@Override
	public Measurement<T> getMax() {
		return members[members.length-1];
	}

	@Override
	public boolean contains(IMeasurement target) {
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
	private boolean partOfRange(IMeasurement target){
		for(IMeasurement value : getMembers()){
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
	private boolean inBetweenRange(IMeasurement target){
		return getMin().compareTo(target) >= 0 && getMax().compareTo(target) <= 0;
	}

}
