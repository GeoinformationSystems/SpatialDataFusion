package de.tudresden.gis.fusion.operation;

import java.util.Arrays;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IMeasurementDescription;
import de.tudresden.gis.fusion.metadata.operation.IMeasurementOperationProfile;
import de.tudresden.gis.fusion.metadata.operation.MeasurementOperationProfile;

public abstract class AMeasurementOperation extends AOperation implements IMeasurementOperation {
	
	@Override
	public IMeasurementOperationProfile getProfile(){
		if(profile == null)
			setProfile(new MeasurementOperationProfile(
					getResource().getIdentifier(),
					new HashSet<IIdentifiableResource>(Arrays.asList(getClassification())),
					getProcessTitle(),
					getProcessAbstract(),
					new HashSet<IIODescription>(Arrays.asList(getInputDescriptions())),
					new HashSet<IIODescription>(Arrays.asList(getOutputDescriptions())),
					new HashSet<IMeasurementDescription>(Arrays.asList(getSupportedMeasurements()))
			));
		return (IMeasurementOperationProfile) profile;
	}
	
	protected abstract IMeasurementDescription[] getSupportedMeasurements();
	
	/**
	 * get measurement description for specified classification
	 * @param identifier classification identifier
	 * @return first measurement description matching the specified classification
	 */
	public IMeasurementDescription getMeasurementDescription(IIRI identifier){
		for(IMeasurementDescription desc : getSupportedMeasurements()){
			if(desc.getRDFRepresentation().getSubject().getIdentifier().equals(identifier))
				return desc;
		}
		return null;
	}
	
}
