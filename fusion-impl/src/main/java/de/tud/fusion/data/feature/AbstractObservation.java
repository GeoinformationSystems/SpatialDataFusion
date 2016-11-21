package de.tud.fusion.data.feature;

import java.util.Set;

import de.tud.fusion.data.IMeasurement;
import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.relation.IFeatureRelation;

public class AbstractObservation extends AbstractFeature implements IObservation {

	public AbstractObservation(String identifier, Object feature, IDataDescription description,	Set<IFeatureRelation> relations) {
		super(identifier, feature, description, relations);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IMeasurement getMeasurement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntityView getFeatureOfInterest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getObservedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMeasurement getPhenomenonTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureConceptView initConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureTypeView initType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureEntityView initEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureRepresentationView initRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
