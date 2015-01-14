package de.tudresden.gis.fusion.data.complex;

import java.util.LinkedHashMap;
import java.util.Map;

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
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

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
	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = new LinkedHashMap<IIdentifiableResource,INode>();
		objectSet.put(new IdentifiableResource(ERDFNamespaces.INSTANCE_OF.asString()), new IdentifiableResource(EFusionNamespace.RDF_TYPE_RELATION_MEASUREMENT.asString()));
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_RELATION_TYPE.asString()), getRelationType());
		objectSet.put(new IdentifiableResource(ERDFNamespaces.HAS_VALUE.asString()), getMeasurementValue());
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
