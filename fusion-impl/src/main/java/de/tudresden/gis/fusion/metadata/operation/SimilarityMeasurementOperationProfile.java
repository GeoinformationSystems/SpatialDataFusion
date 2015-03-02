package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;

public class SimilarityMeasurementOperationProfile extends RelationMeasurementOperationProfile implements ISimilarityMeasurementOperationProfile {

	public SimilarityMeasurementOperationProfile(IIRI iri, Set<IIdentifiableResource> classification, String name, String description, Set<IIODescription> inputs, Set<IIODescription> outputs, Collection<? extends ISimilarityMeasurementDescription> supportedMeasurements) {
		super(iri, classification, name, description, inputs, outputs, supportedMeasurements);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends ISimilarityMeasurementDescription> getSupportedMeasurements(){
		return (Collection<? extends ISimilarityMeasurementDescription>) supportedMeasurements;
	}

}
