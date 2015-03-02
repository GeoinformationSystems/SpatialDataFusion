package de.tudresden.gis.fusion.operation;

import java.util.Arrays;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.operation.ConfidenceMeasurementOperationProfile;
import de.tudresden.gis.fusion.metadata.operation.IConfidenceMeasurementOperationProfile;

public abstract class AConfidenceMeasurementOperation extends ARelationMeasurementOperation implements IConfidenceMeasurementOperation {
	
	@Override
	public IConfidenceMeasurementOperationProfile getProfile(){
		if(profile == null)
			setProfile(new ConfidenceMeasurementOperationProfile(
					getResource().getIdentifier(),
					new HashSet<IIdentifiableResource>(Arrays.asList(getClassification())),
					getProcessTitle(),
					getProcessDescription(),
					new HashSet<IIODescription>(Arrays.asList(getInputDescriptions())),
					new HashSet<IIODescription>(Arrays.asList(getOutputDescriptions())),
					new HashSet<IConfidenceMeasurementDescription>(Arrays.asList(getSupportedMeasurements()))
			));
		return (IConfidenceMeasurementOperationProfile) profile;
	}
	
	@Override
	protected abstract IConfidenceMeasurementDescription[] getSupportedMeasurements();
	
	@Override
	public IConfidenceMeasurementDescription getMeasurementDescription(IIRI identifier){
		for(IConfidenceMeasurementDescription desc : getSupportedMeasurements()){
			if(desc.getRDFRepresentation().getSubject().getIdentifier().equals(identifier))
				return desc;
		}
		return null;
	}
	
}
