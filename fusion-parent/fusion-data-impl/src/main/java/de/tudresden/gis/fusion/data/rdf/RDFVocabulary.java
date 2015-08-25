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
	
	//collection type
	TYPE_BAG("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag"),
	
	//relation measurement types
	TYPE_RELATION_MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion/relation#relationMeasurement"),
	TYPE_MEASUREMENT_DESCRIPTION("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurementDescription"),
	
	//RDF basics
	PREDICATE_TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
	PREDICATE_VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value"),
	
	//dc predicates
	PREDICATE_DESCRIPTION("http://purl.org/dc/terms/description"),
	PREDICATE_TITLE("http://purl.org/dc/elements/1.1/title"),
	PREDICATE_ABSTRACT("http://purl.org/dc/terms/abstract"),
	
	//relation level types
	TYPE_RELATION_FEATURE("http://tu-dresden.de/uw/geo/gis/fusion/relation#featureRelation"),
	TYPE_RELATION_LVL_CONCEPT("http://tu-dresden.de/uw/geo/gis/fusion/relation#conceptRelation"),
	TYPE_RELATION_LVL_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#typeRelation"),
	TYPE_RELATION_LVL_INSTANCE("http://tu-dresden.de/uw/geo/gis/fusion/relation#instanceRelation"),
	TYPE_RELATION_LVL_REPRESENTATION("http://tu-dresden.de/uw/geo/gis/fusion/relation#representationRelation"),
	
	//relation basics
	PREDICATE_RELATION_SOURCE("http://tu-dresden.de/uw/geo/gis/fusion/relation#source"),
	PREDICATE_RELATION_TARGET("http://tu-dresden.de/uw/geo/gis/fusion/relation#target"),
	PREDICATE_RELATION_PROCESS("http://tu-dresden.de/uw/geo/gis/fusion/relation#process"),
	PREDICATE_RELATION_MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurement"),
	PREDICATE_RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#type"),
	PREDICATE_RELATION_MEASUREMENT_UOM("http://tu-dresden.de/uw/geo/gis/fusion/relation#uom"),
	
	//GML geometry types
	TYPE_GML3_0D_POINT("http://www.opengis.net/ont/gml#Point"),
	TYPE_GML3_0D_MULTIPOINT("http://www.opengis.net/ont/gml#MultiPoint"),	
	TYPE_GML3_1D_CURVE("http://www.opengis.net/ont/gml#Curve"),
	TYPE_GML3_1D_MULTICURVE("http://www.opengis.net/ont/gml#MultiCurve"),
	TYPE_GML3_1D_LINESTRING("http://www.opengis.net/ont/gml#LineString"),	
	TYPE_GML3_2D_SURFACE("http://www.opengis.net/ont/gml#Surface"),
	TYPE_GML3_2D_MULTISURFACE("http://www.opengis.net/ont/gml#MultiSurface"),
	TYPE_GML3_2D_POLYGON("http://www.opengis.net/ont/gml#Polygon"),
	TYPE_GML3_COVERAGE_RECTIFIED_GRID("http://schemas.opengis.net/gml/3.2.1/coverage.xsd#RectifiedGridCoverage"),
	TYPE_GML3_GEOMETRY("http://www.opengis.net/ont/geosparql#Geometry"),
	TYPE_GML3_MULTIGEOMETRY("http://www.opengis.net/ont/gml#MultiGeometry"),
	
	//feature properties
	TYPE_PROPERTY_GEOM("http://tu-dresden.de/uw/geo/gis/fusion/relation/property/geometry"),
	TYPE_PROPERTY_THEM("http://tu-dresden.de/uw/geo/gis/fusion/relation/property/thematic"),
	
	//uom - space
	TYPE_UOM_DEGREE_ANGLE("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#DegreeAngle"),
	TYPE_UOM_RADIAN_ANGLE("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Radian"),
	TYPE_UOM_KILOMETER("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Kilometer"),
	
	//uom - time
	TYPE_UOM_MILLISECOND("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#MilliSecond"),
	
	//uom - other
	TYPE_UOM_PERCENT("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Percent"),
	TYPE_UOM_UNDEFINED("http://tu-dresden.de/uw/geo/gis/fusion/uom#undefined"),
	TYPE_UOM_UNKNOWN("http://tu-dresden.de/uw/geo/gis/fusion/uom#unknown"),
	
	//measurement - time
	TYPE_MEAS_TIME_INTERVAL("http://www.w3.org/2006/time#interval"),
	TYPE_MEAS_TIME_INSTANT("http://www.w3.org/2006/time#instant"),
	
	//measurement - topology
	TYPE_MEAS_TOP_INTERSECTS("http://www.opengis.net/ont/geosparql#sfIntersects"),
	TYPE_MEAS_TOP_OVERLAPS("http://www.opengis.net/ont/geosparql#sfOverlaps"),
	TYPE_MEAS_TOP_DE9IM("http://tu-dresden.de/uw/geo/gis/fusion/operation/topology#de9im"),
	
	//measurement - geometry
	TYPE_MEAS_GEOM_DISTANCE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#distance"),
	TYPE_MEAS_GEOM_DISTANCE_HAUSDORFF("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#hausdorffDistance"),
	TYPE_MEAS_GEOM_DIFFERENCE_LENGTH("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#lengthDifference"),
	TYPE_MEAS_GEOM_DIFFERENCE_ANGLE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#angleDifference"),
	TYPE_MEAS_GEOM_DIFFERENCE_SINUOSITY("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#sinuosityDifference"),
	TYPE_MEAS_GEOM_OVERLAP("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#overlap"),
	
	//measurement - thematic
	TYPE_MEAS_STRING_DAMLEV("http://tu-dresden.de/uw/geo/gis/fusion/operation/thematic#damerauLevenshteinDistance"),
	
	;
	
	private IRI identifier;
	private IRDFIdentifiableResource resource;
	
	private RDFVocabulary(String identifier){
		this.identifier = new IRI(identifier);
	}
	
	public IRDFIdentifiableResource resource(){
		if(resource == null)
			resource = new RDFIdentifiableResource(identifier);
		return resource;
	}
	
	public IRDFIdentifiableResource resource(String seperator, String suffix){
		return new RDFIdentifiableResource(identifier + seperator + suffix);
	}
	
	public IRI identifier(){
		return identifier;
	}
	
}
