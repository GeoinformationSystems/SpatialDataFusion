package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

public class SimilarityMeasurementDescription extends RelationMeasurementDescription implements ISimilarityMeasurementDescription {

	public SimilarityMeasurementDescription(IIRI iri, String description, MeasurementRange<?> range, Set<IIdentifiableResource> classification) {
		super(iri, description, range, classification);
	}
	
	public SimilarityMeasurementDescription(IRDFTripleSet decodedRDFResource) throws IOException {
		super(decodedRDFResource);
	}

}
