package de.tudresden.gis.fusion.data.complex;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.manage.DataUtilities;

public class RelationMeasurement implements IResource,IRelationMeasurement,IRDFTripleSet {

	private IIRI iri;
	private IMeasurementValue<?> measurementValue;
	private IMeasurementDescription description;
	
	public RelationMeasurement(IIRI iri, IMeasurementValue<?> measurementValue, IMeasurementDescription description){
		this.iri = iri;
		this.measurementValue = measurementValue;
		this.description = description;
	}
	
	public RelationMeasurement(IMeasurementValue<?> measurementValue, IMeasurementDescription description){
		this(null, measurementValue, description);
	}
	
	@Override
	public IResource getSubject(){
		return this;
	}
	
	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(EFusionNamespace.RDF_TYPE_RELATION_MEASUREMENT.resource()));
		objectSet.put(EFusionNamespace.HAS_RELATION_TYPE.resource(), DataUtilities.toSet(getRelationType()));
		objectSet.put(ERDFNamespaces.HAS_VALUE.resource(), DataUtilities.toSet(getMeasurementValue()));
//		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DESCRIPTION.getURI()), description);
		return objectSet;
	}

	@Override
	public boolean isBlank() {
		return iri == null || iri.toString().isEmpty();
	}
	
	public boolean isResolvable(){
		return true;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public IRelationType getRelationType() {
		return getDescription().getRelationType();
	}

	@Override
	public IMeasurementValue<?> getMeasurementValue() {
		return measurementValue;
	}

	@Override
	public IMeasurementDescription getDescription() {
		return description;
	}

}
