package de.tudresden.gis.fusion.data;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.gis.fusion.data.description.IMeasurementRange;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ITripleSet;
import de.tudresden.gis.fusion.data.rdf.ObjectSet;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.rdf.Resource;

public class MeasurementRange extends Resource implements IMeasurementRange,ITripleSet {

	private ObjectSet objectSet;
	
	//predicates
	private IIdentifiableResource RESOURCE_TYPE = RDFVocabulary.TYPE.asResource();
	private IIdentifiableResource RANGE_MEMBER = RDFVocabulary.RANGE_MEMBER.asResource();
	private IIdentifiableResource CONTINUOUS = RDFVocabulary.RANGE_CONTINUOUS.asResource();
	
	public MeasurementRange(String identifier, TreeSet<? extends IMeasurement> range, boolean continuous){
		super(identifier);
		objectSet = new ObjectSet();
		//set resource type
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.RANGE.asResource());
		//set objects
		objectSet.put(RANGE_MEMBER, range, true);
		objectSet.put(CONTINUOUS, new BooleanLiteral(continuous), true);
	}
	
	public MeasurementRange(TreeSet<? extends IMeasurement> range, boolean continuous){
		this(null, range, continuous);
	}
	
	@Override
	public TreeSet<IMeasurement> getRange() {
		Set<INode> objects = objectSet.get(RANGE_MEMBER);
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
		return ((BooleanLiteral) objectSet.get(CONTINUOUS)).resolve();
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

	@Override
	public Collection<IIdentifiableResource> getPredicates() {
		return objectSet.keySet();
	}

	@Override
	public Set<INode> getObject(IIdentifiableResource predicate) {
		return objectSet.get(predicate);
	}

	@Override
	public int size() {
		return objectSet.numberOfObjects();
	}

}
