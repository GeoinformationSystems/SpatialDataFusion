package de.tudresden.gis.fusion.data.complex;

import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.manage.DataUtilities;

public class SimilarityMeasurement extends RelationMeasurement {

	public SimilarityMeasurement(IIRI iri, IMeasurementValue<?> measurementValue, IMeasurementDescription description) {
		super(iri, measurementValue, description);
	}
	
	public SimilarityMeasurement(IMeasurementValue<?> measurementValue, IMeasurementDescription description) {
		this(null, measurementValue, description);
	}
	
	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(EFusionNamespace.RDF_TYPE_SIMILARITY_MEASUREMENT.resource()));
		return objectSet;
	}

}
