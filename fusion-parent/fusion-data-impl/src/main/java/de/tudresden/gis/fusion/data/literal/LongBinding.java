package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class LongBinding implements ILiteral,IMeasurement<Long>,IRDFTypedLiteral {

	private long value;
	private IRDFIdentifiableResource uom;
	private IDataDescription description;
	private IRange<Long> range;
	
	public LongBinding(long value, IRDFIdentifiableResource uom, IRange<Long> range, IDataDescription description){
		this.value = value;
		this.uom = uom;
		this.range = range;
		this.description = description;
	}
	
	public LongBinding(long value, IRDFIdentifiableResource uom, IDataDescription description){
		this(value, uom, new Range<Long>(new Long[]{Long.MIN_VALUE, Long.MAX_VALUE}, true), description);
	}
	
	public LongBinding(long value, IRDFIdentifiableResource uom){
		this(value, uom, new Range<Long>(new Long[]{Long.MIN_VALUE, Long.MAX_VALUE}, true), null);
	}

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public IDataDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(Long o) {
		return this.getValue().compareTo(o);
	}

	@Override
	public IRange<Long> getRange() {
		return range;
	}

	@Override
	public IRDFIdentifiableResource getUnitOfMeasurement() {
		return uom;
	}

	@Override
	public ILiteral getLiteralValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource getType() {
		return RDFVocabulary.TYPE_INTEGER.resource();
	}
}
