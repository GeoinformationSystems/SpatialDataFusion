package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IIODescription;

public class ConfidenceMeasurementOperationProfile extends RelationMeasurementOperationProfile implements IConfidenceMeasurementOperationProfile {

	public ConfidenceMeasurementOperationProfile(IIRI iri, Set<IIdentifiableResource> classification, String name, String description, Set<IIODescription> inputs, Set<IIODescription> outputs, Collection<? extends IConfidenceMeasurementDescription> supportedMeasurements) {
		super(iri, classification, name, description, inputs, outputs, supportedMeasurements);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends IConfidenceMeasurementDescription> getSupportedMeasurements(){
		return (Collection<? extends IConfidenceMeasurementDescription>) supportedMeasurements;
	}

}
