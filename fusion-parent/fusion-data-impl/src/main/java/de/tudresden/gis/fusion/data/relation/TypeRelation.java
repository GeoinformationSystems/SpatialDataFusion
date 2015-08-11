package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class TypeRelation extends Relation {

	public TypeRelation(IRI identifier, IFeatureType source, IFeatureType target, Collection<IRelationType> types, Collection<IRelationMeasurement<?>> measurements) {
		super(identifier, source, target, types, measurements);
	}
	
	public TypeRelation(IRI identifier, IFeatureType source, IFeatureType target){
		super(identifier, source, target);
	}

	@Override
	public IRDFIdentifiableResource getRelationInstance() {
		return RDFVocabulary.RELATION_LVL_TYPE.resource();
	}

}
