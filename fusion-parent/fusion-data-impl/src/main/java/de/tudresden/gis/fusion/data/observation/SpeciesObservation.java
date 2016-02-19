package de.tudresden.gis.fusion.data.observation;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.literal.TemporalMeasurement;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class SpeciesObservation extends AbstractDataResource implements IObservation {
	
	private SpeciesMeasurement measurement;
	private TemporalMeasurement time;
	
	public SpeciesObservation(String identifier, SpeciesEntity entity, SpeciesMeasurement measurement, TemporalMeasurement time){
		super(identifier, entity);
		this.measurement = measurement;
		this.time = time;
	}
	
	@Override
	public SpeciesEntity resolve(){
		return (SpeciesEntity) super.resolve();
	}

	@Override
	public SpeciesMeasurement getMeasurement() {
		return measurement;
	}

	@Override
	public SpeciesEntity getFeatureOfInterest() {
		return resolve();
	}

	@Override
	public IIdentifiableResource getObservedProperty() {
		return RDFVocabulary.DWC_OCCURRENCE.asResource();
	}

	@Override
	public TemporalMeasurement getPhenomenonTime() {
		return time;
	}

}
