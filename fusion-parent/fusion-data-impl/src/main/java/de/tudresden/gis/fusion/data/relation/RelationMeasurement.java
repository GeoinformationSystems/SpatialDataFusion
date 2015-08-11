package de.tudresden.gis.fusion.data.relation;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFResource;

public class RelationMeasurement<T extends Comparable<T>> extends RDFResource implements IRelationMeasurement<T> {

	private T value;
	IRDFIdentifiableResource uom;
	private IRange<T> range;
	
	public RelationMeasurement(IRI identifier, T value, IRDFIdentifiableResource uom, IRange<T> range) {
		super(identifier);
		this.value = value;
		this.uom = uom;
		this.range = range;
	}

	@Override
	public IRange<T> getRange() {
		return range;
	}

	@Override
	public IRDFIdentifiableResource getUnitOfMeasurement() {
		return uom;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public int compareTo(T o) {
		return getValue().compareTo(o);
	}

}
