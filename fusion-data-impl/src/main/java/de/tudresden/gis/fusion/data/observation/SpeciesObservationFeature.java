package de.tudresden.gis.fusion.data.observation;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AbstractFeature;
import de.tudresden.gis.fusion.data.feature.FeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;

public class SpeciesObservationFeature extends AbstractFeature<SpeciesObservation> {

	public SpeciesObservationFeature(String identifier, SpeciesObservation feature, IDataDescription description) {
		super(identifier, feature, description);
	}

	@Override
	public IFeatureConcept initConcept(SpeciesObservation feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureType initType(SpeciesObservation feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntity initEntity(SpeciesObservation feature) {
		return feature.getFeatureOfInterest();
	}

	@Override
	public IFeatureRepresentation initRepresentation(SpeciesObservation feature) {
		return new FeatureRepresentation(feature);
	}

}
