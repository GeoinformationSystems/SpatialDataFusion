package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;

public class RelationMeasurementDescription extends MeasurementDescription implements IRelationMeasurementDescription {

	Set<IIdentifiableResource> classification;
	
	public RelationMeasurementDescription(IIRI iri, String description, MeasurementRange<?> range, Set<IIdentifiableResource> classification) {
		super(iri, description, range);
		this.classification = classification;
	}
	
	public RelationMeasurementDescription(INode decodedRDFResource) throws IOException {
		super(decodedRDFResource);
	}

	@Override
	public Set<IIdentifiableResource> getRelationTypes() {
		return classification;
	}

}
