package de.tudresden.gis.fusion.metadata;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.metadata.IMeasurementRange;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;

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
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(EFusionNamespace.RDF_TYPE_MEASUREMENT_RANGE.resource()));
		objectSet.put(EFusionNamespace.HAS_MIN.resource(), DataUtilities.toSet(getMin()));
		objectSet.put(EFusionNamespace.HAS_MAX.resource(), DataUtilities.toSet(getMax()));
		objectSet.put(EFusionNamespace.IS_CONTINUOUS.resource(), DataUtilities.toSet(new BooleanLiteral(isContinuous())));
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
