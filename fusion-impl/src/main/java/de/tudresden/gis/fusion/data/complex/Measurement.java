package de.tudresden.gis.fusion.data.complex;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.MeasurementDescription;

public class Measurement extends Resource implements IMeasurement,IRDFTripleSet {

	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_MEASUREMENT.resource();
	private final IIdentifiableResource PROCESS = EFusionNamespace.MEASUREMENT_HAS_PROCESS_URI.resource();
	private final IIdentifiableResource DESCRIPTION = EFusionNamespace.MEASUREMENT_HAS_DESCRIPTION.resource();
	
	private IIdentifiableResource process;
	private IMeasurementValue<?> measurementValue;
	private IMeasurementDescription description;
	
	public Measurement(IIRI iri, IIdentifiableResource process, IMeasurementValue<?> measurementValue, IMeasurementDescription description){
		super(iri);
		this.process = process;
		this.measurementValue = measurementValue;
		this.description = description;
	}
	
	public Measurement(IRDFTripleSet decodedRDFResource) throws IOException {
		//set iri
		super(decodedRDFResource.getSubject().getIdentifier());		
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		//set process resource
		INode nProcess = DataUtilities.getSingleFromObjectSet(objectSet, PROCESS, IIdentifiableResource.class, true);
		this.process = (IIdentifiableResource) nProcess;
		//set value
		INode nValue = DataUtilities.getSingleFromObjectSet(objectSet, ERDFNamespaces.HAS_VALUE.resource(), ITypedLiteral.class, true);
		this.measurementValue = DataUtilities.getMeasurementValue((ITypedLiteral) nValue);
		//set description
		INode nDescription = DataUtilities.getSingleFromObjectSet(objectSet, DESCRIPTION, IRDFTripleSet.class, false);
		if(nDescription != null)
			this.description = new MeasurementDescription((IRDFTripleSet) nDescription);
	}

	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		objectSet.put(PROCESS, DataUtilities.toSet(getProcess()));
		objectSet.put(ERDFNamespaces.HAS_VALUE.resource(), DataUtilities.toSet(getMeasurementValue().getRDFRepresentation()));
//		objectSet.put(DESCRIPTION, DataUtilities.toSet(getDescription().getRDFRepresentation()));
		return objectSet;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public IIdentifiableResource getProcess() {
		return process;
	}

	@Override
	public IMeasurementValue<?> getMeasurementValue() {
		return measurementValue;
	}

	@Override
	public IMeasurementDescription getDescription() {
		return description;
	}
	
	public void setDescription(IMeasurementDescription description) {
		this.description = description;
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

}
