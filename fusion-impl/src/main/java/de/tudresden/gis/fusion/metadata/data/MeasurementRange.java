package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IMeasurementRange;

public class MeasurementRange<T> extends Resource implements IMeasurementRange<T>,IRDFTripleSet {
	
	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_MEASUREMENT_RANGE.resource();
	private final IIdentifiableResource CONTINUOUS = EFusionNamespace.RANGE_IS_CONTINUOUS.resource();
	private final IIdentifiableResource MIN = EFusionNamespace.RANGE_HAS_MIN.resource();
	private final IIdentifiableResource MAX = EFusionNamespace.RANGE_HAS_MAX.resource();
	private final IIdentifiableResource VALUE = EFusionNamespace.RANGE_HAS_VALUE.resource();
	
	private IMeasurementValue<T>[] range;
	private boolean continuous;
	
	public MeasurementRange(IIRI iri, IMeasurementValue<T>[] range, boolean continuous){
		super(iri);
		if(range.length < 2)
			throw new IllegalArgumentException("range must have a minimum length of 2");
		this.range = range;
		Arrays.sort(range);
		this.continuous = continuous;
	}
	
	public MeasurementRange(IMeasurementValue<T>[] range, boolean continuous){
		this(null, range, continuous);
	}
	
	@SuppressWarnings("unchecked")
	public MeasurementRange(IRDFTripleSet decodedRDFResource) throws IOException {		
		//set iri
		super(decodedRDFResource.getSubject().getIdentifier());	
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		//set continuous
		INode nContinuous = DataUtilities.getSingleFromObjectSet(objectSet, CONTINUOUS, BooleanLiteral.class, true);
		continuous = ((BooleanLiteral) nContinuous).getValue();
		//set min
		Set<IMeasurementValue<?>> rangeSet = new HashSet<IMeasurementValue<?>>();
		INode nMin = DataUtilities.getSingleFromObjectSet(objectSet, MIN, ITypedLiteral.class, true);
		rangeSet.add(DataUtilities.getMeasurementValue((ITypedLiteral) nMin));
		//set additional range values
		if(!continuous){	
			Set<INode> values = DataUtilities.getMultipleFromObjectSet(objectSet, VALUE, ITypedLiteral.class, false);
			if(values != null){
				for(INode value : values){
					rangeSet.add(DataUtilities.getMeasurementValue((ITypedLiteral) value));
				}
			}
		}
		//set max
		INode nMax = DataUtilities.getSingleFromObjectSet(objectSet, MAX, ITypedLiteral.class, true);
		rangeSet.add(DataUtilities.getMeasurementValue((ITypedLiteral) nMax));
		//set range
		this.range = rangeSet.toArray(new IMeasurementValue[rangeSet.size()]);
		Arrays.sort(range);
	}

	@Override
	public IMeasurementValue<T>[] getRange() {
		return range;
	}
	
	/**
	 * get range with min and max left out
	 * @return range values, except min and max
	 */
	private IMeasurementValue<T>[] getRangeNoMinMax() {
		return Arrays.copyOfRange(range, 1, range.length-1);
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
	
	/**
	 * check if value is part of range
	 * @param target target value
	 * @return true, if target value is member of range
	 */
	public boolean partOfRange(IMeasurementValue<T> target){
		if(isContinuous())
			return inBetweenRange(target);
		for(IMeasurementValue<T> value : range){
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
	private boolean inBetweenRange(IMeasurementValue<T> target){
		return(getMin().compareTo(target) >= 0 && getMax().compareTo(target) <= 0);
	}

	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		objectSet.put(CONTINUOUS, DataUtilities.toSet(new BooleanLiteral(isContinuous())));
		objectSet.put(MIN, DataUtilities.toSet(getMin().getRDFRepresentation()));
		objectSet.put(MAX, DataUtilities.toSet(getMax().getRDFRepresentation()));
		if(!isContinuous() && getRange().length > 2)
			objectSet.put(VALUE, DataUtilities.toSet(getRangeNoMinMax()));
		return objectSet;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

}
