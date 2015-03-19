package de.tudresden.gis.fusion.data.complex;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IConfidenceMeasurement;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.ConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;

public class ConfidenceMeasurement extends RelationMeasurement implements IConfidenceMeasurement {
	
	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_CONFIDENCE_MEASUREMENT.resource();
	private final IIdentifiableResource DESCRIPTION = EFusionNamespace.MEASUREMENT_HAS_DESCRIPTION.resource();

	public ConfidenceMeasurement(IIRI iri, IIdentifiableResource process, IMeasurementValue<?> measurementValue, IConfidenceMeasurementDescription description){
		super(iri, process, measurementValue, description);
	}
	
	public ConfidenceMeasurement(IMeasurementValue<?> measurementValue, IIdentifiableResource process, IConfidenceMeasurementDescription description){
		this(null, process, measurementValue, description);
	}
	
	public ConfidenceMeasurement(IRDFTripleSet decodedRDFResource) throws IOException {
		super(decodedRDFResource);
		//set description
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		INode nDescription = DataUtilities.getSingleFromObjectSet(objectSet, DESCRIPTION, INode.class, true);
		this.setDescription(new ConfidenceMeasurementDescription(nDescription));
	}
	
	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		return objectSet;
	}
	
	@Override
	public IConfidenceMeasurementDescription getDescription() {
		return (IConfidenceMeasurementDescription) super.getDescription();
	}
	
}
