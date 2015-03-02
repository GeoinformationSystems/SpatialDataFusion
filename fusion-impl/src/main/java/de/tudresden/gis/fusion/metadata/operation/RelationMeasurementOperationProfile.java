package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;
import de.tudresden.gis.fusion.metadata.operation.IRelationMeasurementOperationProfile;

public class RelationMeasurementOperationProfile extends MeasurementOperationProfile implements IRelationMeasurementOperationProfile {

	public RelationMeasurementOperationProfile(IIRI iri, Set<IIdentifiableResource> classification, String name, String description, Set<IIODescription> inputs, Set<IIODescription> outputs, Collection<? extends IRelationMeasurementDescription> supportedMeasurements) {
		super(iri, classification, name, description, inputs, outputs, supportedMeasurements);
	}
	
	@Override
	public Collection<? extends IRelationMeasurementDescription> getSupportedMeasurements(){
		return (Collection<? extends IRelationMeasurementDescription>) getSupportedMeasurements();
	}

}
