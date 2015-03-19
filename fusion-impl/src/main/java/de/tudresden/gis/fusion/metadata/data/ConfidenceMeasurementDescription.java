package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;

public class ConfidenceMeasurementDescription extends RelationMeasurementDescription implements IConfidenceMeasurementDescription {

	public ConfidenceMeasurementDescription(IIRI iri, String description, MeasurementRange<?> range, Set<IIdentifiableResource> classification) {
		super(iri, description, range, classification);
	}
	
	public ConfidenceMeasurementDescription(INode decodedRDFResource) throws IOException {
		super(decodedRDFResource);
	}

}
