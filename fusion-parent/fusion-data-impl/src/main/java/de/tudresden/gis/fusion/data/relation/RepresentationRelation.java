package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class RepresentationRelation extends Relation {

	public RepresentationRelation(IRI identifier, IFeatureRepresentation source, IFeatureRepresentation target, Collection<IRelationType> types, Collection<IRelationMeasurement<?>> measurements) {
		super(identifier, source, target, types, measurements);
	}
	
	public RepresentationRelation(IRI identifier, IFeatureRepresentation source, IFeatureRepresentation target){
		super(identifier, source, target);
	}

	@Override
	public IRDFIdentifiableResource getRelationInstance() {
		return RDFVocabulary.RELATION_LVL_REPRESENTATION.resource();
	}

}
