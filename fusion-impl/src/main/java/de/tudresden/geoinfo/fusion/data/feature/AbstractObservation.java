package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IMeasurementData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Set;

public class AbstractObservation extends AbstractFeature implements IObservation {

	public AbstractObservation(IIdentifier identifier, Object feature, IMetadataForData description, Set<IRelation<? extends IFeature>> relations) {
		super(identifier, feature, description, relations);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IMeasurementData getMeasurement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntity getFeatureOfInterest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getObservedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMeasurementData getPhenomenonTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureConcept initConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureType initType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureEntity initEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureRepresentation initRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
