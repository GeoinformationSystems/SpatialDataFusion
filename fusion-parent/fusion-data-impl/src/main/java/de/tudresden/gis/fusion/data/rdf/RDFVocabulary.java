package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.IRI;

public enum RDFVocabulary {

	//RDF literal types
	TYPE_BOOLEAN("http://www.w3.org/2001/XMLSchema/#boolean"),
	TYPE_INTEGER("http://www.w3.org/2001/XMLSchema/#integer"),
	TYPE_LONG("http://www.w3.org/2001/XMLSchema/#long"),
	TYPE_DECIMAL("http://www.w3.org/2001/XMLSchema/#decimal"),
	TYPE_STRING("http://www.w3.org/2001/XMLSchema/#string"),
	TYPE_ANYURI("http://www.w3.org/2001/XMLSchema/#anyURI"),
	
	//RDF basics
	PREDICATE_TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
	PREDICATE_VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value"),
	
	//relation level types
	RELATION_LVL_CONCEPT("http://tu-dresden.de/uw/geo/gis/fusion/relation#concept"),
	RELATION_LVL_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#type"),
	RELATION_LVL_INSTANCE("http://tu-dresden.de/uw/geo/gis/fusion/relation#instance"),
	RELATION_LVL_REPRESENTATION("http://tu-dresden.de/uw/geo/gis/fusion/relation#representation"),
	
	//relation basics
	RELATION_SOURCE("http://tu-dresden.de/uw/geo/gis/fusion/relation#hasSource"),
	RELATION_TARGET("http://tu-dresden.de/uw/geo/gis/fusion/relation#hasTarget"),
	RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#hasType"),
	RELATION_MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion/relation#hasMeasurement"),
	
	//uom
	UOM_MILLISECOND("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#MilliSecond"),
	
	;
	
	private IRDFIdentifiableResource resource;
	
	private RDFVocabulary(String identifier){
		this.resource = new RDFIdentifiableResource(new IRI(identifier));
	}
	
	public IRDFIdentifiableResource resource(){
		return resource;
	}
	
}
