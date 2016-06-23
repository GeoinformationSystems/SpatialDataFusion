package de.tudresden.gis.fusion.data.observation;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AbstractFeature;
import de.tudresden.gis.fusion.data.feature.FeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;

/**
 * observed feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class SpeciesObservationFeature extends AbstractFeature<SpeciesObservation> {

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param observation observation object
	 * @param description observation description
	 */
	public SpeciesObservationFeature(String identifier, SpeciesObservation observation, IDataDescription description) {
		super(identifier, observation, description);
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
