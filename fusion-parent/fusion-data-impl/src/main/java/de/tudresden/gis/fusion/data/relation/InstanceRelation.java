package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.IFeatureInstance;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class InstanceRelation extends Relation {

	public InstanceRelation(IRI identifier, IFeatureInstance source, IFeatureInstance target, Collection<IRelationType> types, Collection<IRelationMeasurement<?>> measurements) {
		super(identifier, source, target, types, measurements);
	}
	
	public InstanceRelation(IRI identifier, IFeatureInstance source, IFeatureInstance target){
		super(identifier, source, target);
	}

	@Override
	public IRDFIdentifiableResource getRelationInstance() {
		return RDFVocabulary.RELATION_LVL_INSTANCE.resource();
	}

}
