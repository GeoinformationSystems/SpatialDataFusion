package de.tudresden.gis.fusion.data.rdf;

public enum EFusionNamespace {

	//relation predicates
	HAS_REFERENCE("hasReference"),
	HAS_TARGET("hasTarget"),
	HAS_RELATION_MEASUREMENT("hasRelationMeasurement"),
	
	//description predicates
	HAS_RELATION_TYPE("hasRelationType"),
	HAS_TITLE("hasTitle"),
	HAS_DESCRIPTION("hasDescription"),
	HAS_ABSTRACT("hasAbstract"),
	HAS_PROCESS_URI("hasProcessURI"),
	HAS_MEMBER("hasMember"),
	HAS_RANGE("hasRange"),
	HAS_MIN("hasMin"),
	HAS_MAX("hasMax"),
	IS_CONTINUOUS("isContinuous"),
	HAS_IDENTIFIER("hasIdentifier"),
	SUPPORTED_MEASUREMENT("supportedMeasurement"),
	HAS_INPUT("hasInput"),
	HAS_OUTPUT("hasOutput"),
	IS_MANDATORY("isMandatory"),
	HAS_MIN_OCCURRENCE("hasMinOccurrence"),
	HAS_MAX_OCCURRENCE("hasMaxOccurrence"),
	HAS_BINDING("hasBinding"),
	HAS_RESTRICTION("hasRestriction"),
	HAS_DEFAULT("hasDefault"),
	
	//relation element types
	RDF_TYPE_RELATION_MEASUREMENT("relationMeasurement"),
	RDF_TYPE_SIMILARITY_MEASUREMENT("similarityMeasurement"),
	RDF_TYPE_CONFIDENCE_MEASUREMENT("confidenceMeasurement"),
	RDF_TYPE_FEATURE_RELATION("featureRelation"),	
	RDF_TYPE_MEASUREMENT_RANGE("measurementRange");
	
	private final String BASE = "http://tu-dresden.de/uw/geo/gis/fusion#";
	private IIdentifiableResource resource;
	
	private EFusionNamespace(String identifier){
		this.resource = new IdentifiableResource(BASE + identifier);
	}
	
	public IIdentifiableResource resource(){
		return resource;
	}
	
	public static IIdentifiableResource resource4Identifier(IIRI identifier){
		for(EFusionNamespace namespace : EFusionNamespace.values()){
			if(namespace.resource().getIdentifier().equals(identifier))
				return namespace.resource();
		}
		return null;
	}
}
