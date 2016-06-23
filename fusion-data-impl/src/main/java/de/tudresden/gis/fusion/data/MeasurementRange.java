package de.tudresden.gis.fusion.data;

import java.util.Set;
import java.util.TreeSet;

import de.tudresden.gis.fusion.data.description.IMeasurementRange;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Subject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class MeasurementRange extends Subject implements IMeasurementRange {
	
	private IResource RANGE_MEMBER = RDFVocabulary.RANGE_MEMBER.getResource();
	private IResource CONTINUOUS = RDFVocabulary.RANGE_CONTINUOUS.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param range measurement range
	 * @param continuous true for continuous range
	 */
	public MeasurementRange(String identifier, TreeSet<? extends IMeasurement> range, boolean continuous){
		super(identifier);
		//set resource type
		this.put(RDFVocabulary.TYPE.getResource(), RDFVocabulary.RANGE.getResource());
		//set objects
		this.put(RDFVocabulary.RANGE_MEMBER.getResource(), range, true);
		this.put(RDFVocabulary.RANGE_CONTINUOUS.getResource(), new BooleanLiteral(continuous), true);
	}
	
	/**
	 * constructor
	 * @param range measurement range
	 * @param continuous true for continuous range
	 */
	public MeasurementRange(TreeSet<? extends IMeasurement> range, boolean continuous){
		this(null, range, continuous);
	}
	
	@Override
	public TreeSet<IMeasurement> getRange() {
		Set<INode> objects = this.getObjects(RANGE_MEMBER);
		TreeSet<IMeasurement> measurements = new TreeSet<IMeasurement>();
		for(INode object : objects){
			if(object instanceof IMeasurement)
				measurements.add((IMeasurement) object);
			else //should not happen
				throw new RuntimeException("node does not implement IMeasurement");
		}
		return(measurements);
	}

	@Override
	public boolean isContinuous() {
		return ((BooleanLiteral) this.getObjects(CONTINUOUS)).resolve();
	}

	@Override
	public IMeasurement getMin() {
		return getRange().first();
	}

	@Override
	public IMeasurement getMax() {
		return getRange().last();
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
		for(IMeasurement value : getRange()){
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
	private boolean inBetweenRange(IMeasurement target){
		return(getMin().compareTo(target) >= 0 && getMax().compareTo(target) <= 0);
	}

}
