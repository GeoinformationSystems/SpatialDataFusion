package de.tudresden.gis.fusion.data.rdf.namespace;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public enum EFusionNamespace {
	
	//feature relation types
	RDF_TYPE_FEATURE_RELATION("featureRelation"),
	RELATION_HAS_REFERENCE("relationHasReference"),
	RELATION_HAS_TARGET("relationHasTarget"),
	RELATION_HAS_RELATION_MEASUREMENT("relationHasRelationMeasurement"),
	
	//specific relation measurement types
	RDF_TYPE_SIMILARITY_MEASUREMENT("similarityMeasurement"),
	RDF_TYPE_CONFIDENCE_MEASUREMENT("confidenceMeasurement"),
	
	//relation measurement types
	RDF_TYPE_RELATION_MEASUREMENT("relationMeasurement"),
	
	//measurement types
	RDF_TYPE_MEASUREMENT("measurement"),
	MEASUREMENT_HAS_PROCESS_URI("measurementHasProcessURI"),
	MEASUREMENT_HAS_DESCRIPTION("measurementHasDescription"),
	
	//measurement description types
	RDF_TYPE_MEASUREMENT_DESCRIPTION("measurementDescription"),
	MEASUREMENT_DESCRIPTION_HAS_RANGE("hasMeasurementRange"),
	
	//description types
	RDF_TYPE_DESCRIPTION("description"),
	DESCRIPTION_HAS_ABSTRACT("descriptionHasAbstract"),
	
	//measurement range types
	RDF_TYPE_MEASUREMENT_RANGE("measurementRange"),
	RANGE_IS_CONTINUOUS("rangeIsContinuous"),
	RANGE_HAS_VALUE("rangeHasValue"),
	RANGE_HAS_MIN("rangeHasMin"),
	RANGE_HAS_MAX("rangeHasMax"),
	
	//misc
	HAS_RELATION_TYPE("hasRelationType"),
	HAS_TITLE("hasTitle"),	
	HAS_ABSTRACT("hasAbstract"),	
	HAS_MEMBER("hasMember"),
	HAS_DESCRIPTION("hasDescription"),
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
	
	;
	
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
