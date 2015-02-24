package de.tudresden.gis.fusion.operation;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementOperationProfile;
import de.tudresden.gis.fusion.operation.IMeasurement;
import de.tudresden.gis.fusion.operation.metadata.IMeasurementProfile;

public abstract class AbstractMeasurementOperation extends AbstractOperation implements IMeasurement {
	
	public IMeasurementProfile getProfile(){
		return (IMeasurementProfile) super.getProfile();
	}
	
	protected void initDescription(){
		setDescription(new MeasurementOperationProfile(
				getProcessIRI(),
				getProcessTitle(),
				getProcessDescription(),
				getInputDescriptions(),
				getOutputDescriptions(),
				getSupportedMeasurements()
		));
	}
	
	protected abstract Collection<IMeasurementDescription> getSupportedMeasurements();
	
	/**
	 * get measurement description for particular relation type
	 * @param relationType input relation type
	 * @return measurement description
	 */
	public IMeasurementDescription getMeasurementDescription(IRelationType relationType){
		for(IMeasurementDescription desc : getSupportedMeasurements()){
			if(desc.getRelationType().equals(relationType))
				return desc;
		}
		return null;
	}
	
}
