package de.tudresden.gis.fusion.data.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class MeasurementDescription extends DataDescription implements IMeasurementDescription {
	
	private IRange<?> range;
	private IRDFIdentifiableResource uom;

	public MeasurementDescription(IRI identifier, String title, String abstrakt, IRange<?> range, IRDFIdentifiableResource uom, IDataProvenance provenance) {
		super(identifier, title, abstrakt, provenance);
		this.range = range;
		this.uom = uom;
	}
	
	@Override
	public IRange<?> range() {
		return range;
	}

	@Override
	public IRDFIdentifiableResource unitOfMeasurement() {
		return uom;
	}
	
	@Override
	public Collection<IRDFPredicateObject> objectSet() {
		Collection<IRDFPredicateObject> objectSet = super.objectSet();
		
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_MEASUREMENT_UOM.resource(), this.unitOfMeasurement()));
		return objectSet;
	}

}
