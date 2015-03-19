package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IMeasurementDescription;
import de.tudresden.gis.fusion.metadata.operation.IMeasurementOperationProfile;

public class MeasurementOperationProfile extends OperationProfile implements IMeasurementOperationProfile {

	protected Collection<? extends IMeasurementDescription> supportedMeasurements;
	
	public MeasurementOperationProfile(IIRI iri, Set<IIdentifiableResource> classification, String name, String description, Set<IIODescription> inputs, Set<IIODescription> outputs, Collection<? extends IMeasurementDescription> supportedMeasurements) {
		super(iri, classification, name, description, inputs, outputs);
		this.supportedMeasurements = supportedMeasurements;
	}
	
	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(EFusionNamespace.SUPPORTED_MEASUREMENT.resource(), DataUtilities.descriptionsToNodeSet(supportedMeasurements));
		return objectSet;
	}

	public Collection<? extends IMeasurementDescription> getSupportedMeasurements(){
		return supportedMeasurements;
	}

}
