package de.tudresden.gis.fusion.data.observation;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AbstractFeature;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.observation.IObservation;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class Observation<T extends Object> extends AbstractFeature<T> implements IObservation {

	public Observation(String identifier, T feature, IDataDescription description) {
		super(identifier, feature, description);
	}

	@Override
	public IMeasurement getMeasurement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntity getFeatureOfInterest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIdentifiableResource getObservedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMeasurement getPhenomenonTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureConcept initConcept(T feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureType initType(T feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntity initEntity(T feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureRepresentation initRepresentation(T feature) {
		// TODO Auto-generated method stub
		return null;
	}

}
