package de.tudresden.gis.fusion.data.complex;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.RelationMeasurementDescription;

public class RelationMeasurement extends Measurement implements IRelationMeasurement {

	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_RELATION_MEASUREMENT.resource();
	private final IIdentifiableResource DESCRIPTION = EFusionNamespace.MEASUREMENT_HAS_DESCRIPTION.resource();
	
	public RelationMeasurement(IIRI iri, IIdentifiableResource process, IMeasurementValue<?> measurementValue, IRelationMeasurementDescription description){
		super(iri, process, measurementValue, description);
	}
	
	public RelationMeasurement(IRDFTripleSet decodedRDFResource) throws IOException {
		super(decodedRDFResource);
		//set description
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		INode nDescription = DataUtilities.getSingleFromObjectSet(objectSet, DESCRIPTION, INode.class, true);
		this.setDescription(new RelationMeasurementDescription(nDescription));
	}
	
	public RelationMeasurement(IMeasurementValue<?> measurementValue, IIdentifiableResource process, IRelationMeasurementDescription description){
		this(null, process, measurementValue, description);
	}
	
	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		return objectSet;
	}
	
	public boolean isResolvable(){
		return true;
	}

	@Override
	public IRelationMeasurementDescription getDescription() {
		return (IRelationMeasurementDescription) super.getDescription();
	}

}
