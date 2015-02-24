package de.tudresden.gis.fusion.metadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;
import de.tudresden.gis.fusion.operation.metadata.IMeasurementProfile;

public class MeasurementOperationProfile extends OperationProfile implements IMeasurementProfile {

	private Collection<IMeasurementDescription> supportedMeasurements;
	
	public MeasurementOperationProfile(IIRI iri, String name, String description, Collection<IIODescription> inputs, Collection<IIODescription> outputs, Collection<IMeasurementDescription> supportedMeasurements) {
		super(iri, name, description, inputs, outputs);
		this.supportedMeasurements = supportedMeasurements;
	}
	
	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(EFusionNamespace.SUPPORTED_MEASUREMENT.resource(), DataUtilities.collectionToSet(supportedMeasurements));
		return objectSet;
	}

	public Collection<IMeasurementDescription> getSupportedMeasurements(){
		return supportedMeasurements;
	}

}
