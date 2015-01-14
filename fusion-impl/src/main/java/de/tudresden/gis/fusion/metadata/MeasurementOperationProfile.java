package de.tudresden.gis.fusion.metadata;

import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;
import de.tudresden.gis.fusion.operation.metadata.IMeasurementOperationProfile;

public class MeasurementOperationProfile extends OperationProfile implements IMeasurementOperationProfile {

	private Collection<IMeasurementDescription> supportedMeasurements;
	
	public MeasurementOperationProfile(IIRI iri, String name, String description, Collection<IIODescription> inputs, Collection<IIODescription> outputs, Collection<IMeasurementDescription> supportedMeasurements) {
		super(iri, name, description, inputs, outputs);
		this.supportedMeasurements = supportedMeasurements;
	}
	
	@Override
	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = super.getObjectSet();
		for(IMeasurementDescription measurement : supportedMeasurements){
			objectSet.put(new IdentifiableResource(EFusionNamespace.SUPPORTED_MEASUREMENT.asString()), measurement);
		}
		return objectSet;
	}

	public Collection<IMeasurementDescription> getSupportedMeasurements(){
		return supportedMeasurements;
	}

}
