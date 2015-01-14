package de.tudresden.gis.fusion.data.complex;

import java.util.Map;

import de.tudresden.gis.fusion.data.IConfidenceMeasurement;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class ConfidenceMeasurement extends RelationMeasurement implements IConfidenceMeasurement {

	public ConfidenceMeasurement(IIRI iri, IMeasurementValue<?> measurementValue, IMeasurementDescription description) {
		super(iri, measurementValue, description);
	}
	
	public ConfidenceMeasurement(IMeasurementValue<?> measurementValue, IMeasurementDescription description) {
		this(null, measurementValue, description);
	}
	
	@Override
	public Map<IIdentifiableResource, INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = super.getObjectSet();
		//replace relation measurement type with similarity measurement type
		IdentifiableResource rmKey = (IdentifiableResource) objectSet.keySet().iterator().next(); //first key must be type definition
		objectSet.put(rmKey, new IdentifiableResource(EFusionNamespace.RDF_TYPE_CONFIDENCE_MEASUREMENT.asString()));
		return objectSet;
	}
	
}
