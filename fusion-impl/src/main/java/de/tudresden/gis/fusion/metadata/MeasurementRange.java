package de.tudresden.gis.fusion.metadata;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.metadata.IMeasurementRange;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;

public class MeasurementRange<T> implements IMeasurementRange<T>,IRDFTripleSet {
	
	private IMeasurementValue<T>[] range;
	private boolean continuous;
	
	public MeasurementRange(IMeasurementValue<T>[] range, boolean continuous){
		if(range.length < 2)
			throw new IllegalArgumentException("range must have a length of 2 or more");
		this.range = range;
		Arrays.sort(range);
		this.continuous = continuous;
	}

	@Override
	public IMeasurementValue<T>[] getRange() {
		return range;
	}

	@Override
	public boolean isContinuous() {
		return continuous;
	}

	@Override
	public IMeasurementValue<T> getMin() {
		return range[0];
	}

	@Override
	public IMeasurementValue<T> getMax() {
		return range[range.length-1];
	}

	@Override
	public boolean contains(IMeasurementValue<T> target) {
		if(!isContinuous())
			return partOfRange(target);
		else
			return inBetweenRange(target);
	}
	
	public boolean partOfRange(IMeasurementValue<T> target){
		for(IMeasurementValue<T> value : range){
			if(value.equals(target))
				return true;
		}
		return false;
	}
	
	private boolean inBetweenRange(IMeasurementValue<T> target){
		return(getMin().compareTo(target) >= 0 && getMax().compareTo(target) <= 0);
	}

	@Override
	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = new LinkedHashMap<IIdentifiableResource,INode>();
		objectSet.put(new IdentifiableResource(ERDFNamespaces.INSTANCE_OF.asString()), new IdentifiableResource(EFusionNamespace.RDF_TYPE_MEASUREMENT_RANGE.asString()));
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_MIN.asString()), getMin());
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_MAX.asString()), getMax());
		objectSet.put(new IdentifiableResource(EFusionNamespace.IS_CONTINUOUS.asString()), new BooleanLiteral(isContinuous()));
		return objectSet;
	}

	@Override
	public IResource getSubject() {
		return Resource.newEmptyResource();
	}

	@Override
	public Object getIdentifier() {
		return this.getSubject().getIdentifier();
	}

}
