package de.tudresden.gis.fusion.operation;

import java.util.Arrays;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;
import de.tudresden.gis.fusion.metadata.operation.IRelationMeasurementOperationProfile;
import de.tudresden.gis.fusion.metadata.operation.RelationMeasurementOperationProfile;

public abstract class ARelationMeasurementOperation extends AMeasurementOperation implements IRelationMeasurementOperation {
	
	@Override
	public IRelationMeasurementOperationProfile getProfile(){
		if(profile == null)
			setProfile(new RelationMeasurementOperationProfile(
					getResource().getIdentifier(),
					new HashSet<IIdentifiableResource>(Arrays.asList(getClassification())),
					getProcessTitle(),
					getProcessDescription(),
					new HashSet<IIODescription>(Arrays.asList(getInputDescriptions())),
					new HashSet<IIODescription>(Arrays.asList(getOutputDescriptions())),
					new HashSet<IRelationMeasurementDescription>(Arrays.asList(getSupportedMeasurements()))
			));
		return (IRelationMeasurementOperationProfile) profile;
	}
	
	@Override
	protected abstract IRelationMeasurementDescription[] getSupportedMeasurements();
	
	/**
	 * get measurement description for specified classification
	 * @param identifier classification identifier
	 * @return first measurement description matching the specified classification
	 */
	public IRelationMeasurementDescription getMeasurementDescription(IIRI identifier){
		for(IRelationMeasurementDescription desc : getSupportedMeasurements()){
			if(desc.getRDFRepresentation().getSubject().getIdentifier().equals(identifier))
				return desc;
		}
		return null;
	}
	
}
