package de.tudresden.gis.fusion.data.observation;

import java.util.Set;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.feature.IObservation;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.literal.TemporalMeasurement;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

/**
 * observation of a species
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class SpeciesObservation extends AbstractDataResource implements IObservation {
	
	/**
	 * species measurement
	 */
	private SpeciesMeasurement measurement;
	
	/**
	 * time instant of the observation
	 */
	private TemporalMeasurement time;
	
	/**
	 * constructor
	 * @param identifier observation resource identifier
	 * @param entity observed feature entity
	 * @param measurement observation measurement
	 * @param time observation time instant
	 */
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
	public IResource getObservedProperty() {
		return RDFVocabulary.DWC_OCCURRENCE.getResource();
	}

	@Override
	public TemporalMeasurement getPhenomenonTime() {
		return time;
	}

	@Override
	public IFeatureConcept getConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntity getEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureRepresentation getRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IFeatureRelation> getRelations() {
		// TODO Auto-generated method stub
		return null;
	}

}
