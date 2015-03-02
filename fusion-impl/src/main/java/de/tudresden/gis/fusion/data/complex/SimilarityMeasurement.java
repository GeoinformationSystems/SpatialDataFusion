package de.tudresden.gis.fusion.data.complex;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.ISimilarityMeasurement;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.SimilarityMeasurementDescription;

public class SimilarityMeasurement extends RelationMeasurement implements ISimilarityMeasurement {
	
	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_SIMILARITY_MEASUREMENT.resource();
	private final IIdentifiableResource DESCRIPTION = EFusionNamespace.MEASUREMENT_HAS_DESCRIPTION.resource();

	public SimilarityMeasurement(IIRI iri, IIdentifiableResource process, IMeasurementValue<?> measurementValue, ISimilarityMeasurementDescription description){
		super(iri, process, measurementValue, description);
	}
	
	public SimilarityMeasurement(IMeasurementValue<?> measurementValue, IIdentifiableResource process, ISimilarityMeasurementDescription description){
		this(null, process, measurementValue, description);
	}
	
	public SimilarityMeasurement(IRDFTripleSet decodedRDFResource) throws IOException {
		super(decodedRDFResource);
		//set description
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		INode nDescription = DataUtilities.getSingleFromObjectSet(objectSet, DESCRIPTION, IRDFTripleSet.class, true);
		this.setDescription(new SimilarityMeasurementDescription((IRDFTripleSet) nDescription));
	}
	
	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		return objectSet;
	}
	
	@Override
	public ISimilarityMeasurementDescription getDescription() {
		return (ISimilarityMeasurementDescription) super.getDescription();
	}

}
