package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class ConceptRelation extends Relation {

	public ConceptRelation(IRI identifier, IFeatureConcept source, IFeatureConcept target, Collection<IRelationType> types, Collection<IRelationMeasurement<?>> measurements) {
		super(identifier, source, target, types, measurements);
	}
	
	public ConceptRelation(IRI identifier, IFeatureConcept source, IFeatureConcept target){
		super(identifier, source, target);
	}

	@Override
	public IRDFIdentifiableResource getRelationInstance() {
		return RDFVocabulary.RELATION_LVL_CONCEPT.resource();
	}

}
