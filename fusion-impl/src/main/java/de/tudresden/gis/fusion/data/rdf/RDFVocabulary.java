package de.tudresden.gis.fusion.data.rdf;

/**
 * RDF vocabulary
 * @author Stefan Wiemann, TU Dresden
 *
 */
public enum RDFVocabulary {
	
	/**
	 * namespaces
	 */
	
	RDF("http://www.w3.org/1999/02/22-rdf-syntax-ns"),
	
	/**
	 * RDF predicates
	 */
	
	TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
	VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value"),
	
	/**
	 * RDF Literal types
	 */
	
	BOOLEAN("http://www.w3.org/2001/XMLSchema/#boolean"),
	INTEGER("http://www.w3.org/2001/XMLSchema/#integer"),
	LONG("http://www.w3.org/2001/XMLSchema/#long"),
	DECIMAL("http://www.w3.org/2001/XMLSchema/#decimal"),
	STRING("http://www.w3.org/2001/XMLSchema/#string"),
	ANYURI("http://www.w3.org/2001/XMLSchema/#anyURI"),
	
	/**
	 * RDF collection types
	 */
	
	BAG("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag"),
	MEMBER("http://www.w3.org/1999/02/22-rdf-syntax-ns#li"),
	
	/**
	 * W3C Geo Vocabular
	 */
	
	W3C_GEO_LAT("http://www.w3.org/2003/01/geo/wgs84_pos#lat"),
	W3C_GEO_LON("http://www.w3.org/2003/01/geo/wgs84_pos#long"),
	
	/**
	 * OGC GeoSPARQL vocabulary
	 */
	
	GEOMETRY("http://www.opengis.net/ont/geosparql#Geometry"),
	WKT("http://www.opengis.net/ont/geosparql#asWKT"),
	WKT_LITERAL("http://www.opengis.net/ont/geosparql#wktLiteral"),
	SF_INTERSECTS("http://www.opengis.net/ont/geosparql#sfIntersects"),
	SF_OVERLAPS("http://www.opengis.net/ont/geosparql#sfOverlaps"),
	
	/**
	 * OGC GML geometry types
	 */
	
	GML3_0D_POINT("http://www.opengis.net/ont/gml#Point"),
	GML3_0D_MULTIPOINT("http://www.opengis.net/ont/gml#MultiPoint"),	
	GML3_1D_CURVE("http://www.opengis.net/ont/gml#Curve"),
	GML3_1D_MULTICURVE("http://www.opengis.net/ont/gml#MultiCurve"),
	GML3_1D_LINESTRING("http://www.opengis.net/ont/gml#LineString"),	
	GML3_2D_SURFACE("http://www.opengis.net/ont/gml#Surface"),
	GML3_2D_MULTISURFACE("http://www.opengis.net/ont/gml#MultiSurface"),
	GML3_2D_POLYGON("http://www.opengis.net/ont/gml#Polygon"),
	GML3_COVERAGE_RECTIFIED_GRID("http://schemas.opengis.net/gml/3.2.1/coverage.xsd#RectifiedGridCoverage"),
	GML3_GEOMETRY("http://www.opengis.net/ont/geosparql#Geometry"),
	GML3_MULTIGEOMETRY("http://www.opengis.net/ont/gml#MultiGeometry"),
	
	/**
	 * QUDT units of measurement
	 */
	
	UOM_DEGREE_ANGLE("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#DegreeAngle"),
	UOM_RADIAN_ANGLE("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Radian"),
	UOM_KILOMETER("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Kilometer"),
	UOM_MILLISECOND("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#MilliSecond"),
	UOM_PERCENT("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Percent"),
	UOM_NUMBER("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Number"),
	UOM_UNDEFINED("http://tu-dresden.de/uw/geo/gis/fusion/uom#undefined"),
	UOM_UNKNOWN("http://tu-dresden.de/uw/geo/gis/fusion/uom#unknown"),
	UOM_MAP_UNITS("http://tu-dresden.de/uw/geo/gis/fusion/uom#mapUnits"),
	
	/**
	 * Darwin Core
	 */
		
	DWC_OCCURRENCE("http://rs.tdwg.org/dwc/terms/Occurrence"),
	
	/**
	 * Dublin Core
	 */
		
	DC_DESCRIPTION("http://purl.org/dc/terms/description"),
	DC_TITLE("http://purl.org/dc/elements/1.1/title"),
	DC_ABSTRACT("http://purl.org/dc/terms/abstract"),
		
	/**
	 * W3C time vocabulary
	 */
	
	TIME_INTERVAL("http://www.w3.org/2006/time#interval"),
	TIME_INSTANT("http://www.w3.org/2006/time#instant"),
	
	/**
	 * **********************************************
	 * fusion specific vocabulary		*************
	 * **********************************************
	 */
	
	/**
	 * measurement range
	 */
	
	RANGE("http://tu-dresden.de/uw/geo/gis/fusion#range"),
	RANGE_MEMBER("http://tu-dresden.de/uw/geo/gis/fusion#rangeMember"),
	RANGE_CONTINUOUS("http://tu-dresden.de/uw/geo/gis/fusion#rangeContinuous"),
	
	/**
	 * feature and feature views
	 */
	
	FEATURE("http://tu-dresden.de/uw/geo/gis/fusion#feature"),
	COVERAGE("http://tu-dresden.de/uw/geo/gis/fusion#coverage"),
	FEATURE_CONCEPT("http://tu-dresden.de/uw/geo/gis/fusion#featureConcept"),
	FEATURE_TYPE("http://tu-dresden.de/uw/geo/gis/fusion#featureType"),
	FEATURE_ENTITY("http://tu-dresden.de/uw/geo/gis/fusion#featureInstance"),
	FEATURE_REPRESENTATION("http://tu-dresden.de/uw/geo/gis/fusion#featureRepresentation"),
	
	/**
	 * feature properties
	 */

	PROPERTY_GEOM("http://tu-dresden.de/uw/geo/gis/fusion#geometryProperty"),
	PROPERTY_THEM("http://tu-dresden.de/uw/geo/gis/fusion#thematicProperty"),
	
	/**
	 * feature relation
	 */
	
	FEATURE_RELATION("http://tu-dresden.de/uw/geo/gis/fusion#featureRelation"),
	RELATION_MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion#relationMeasurement"),
	MEASUREMENT_DESCRIPTION("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurementDescription"),
	RELATION_REFERENCE("http://tu-dresden.de/uw/geo/gis/fusion/relation#reference"),
	RELATION_TARGET("http://tu-dresden.de/uw/geo/gis/fusion/relation#target"),
	RELATION_VIEW("http://tu-dresden.de/uw/geo/gis/fusion/relation#featureView"),
	PREDICATE_RELATION_PROCESS("http://tu-dresden.de/uw/geo/gis/fusion/relation#process"),
	
	RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#type"),
	PREDICATE_RELATION_MEASUREMENT_UOM("http://tu-dresden.de/uw/geo/gis/fusion/relation#uom"),
	
	/**
	 * description
	 */
	
	METADATA("http://tu-dresden.de/uw/geo/gis/fusion#metadata"),

	/**
	 * operations
	 */
	
	//measurement - topology
	TYPE_MEAS_TOP_DE9IM("http://tu-dresden.de/uw/geo/gis/fusion/operation/topology#de9im"),
	
	//measurement - geometry
	TYPE_MEAS_GEOM_DISTANCE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#distance"),
	TYPE_MEAS_GEOM_DISTANCE_HAUSDORFF("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#hausdorffDistance"),
	TYPE_MEAS_GEOM_DIFFERENCE_LENGTH("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#lengthDifference"),
	TYPE_MEAS_GEOM_DIFFERENCE_ANGLE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#angleDifference"),
	TYPE_MEAS_GEOM_DIFFERENCE_SINUOSITY("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#sinuosityDifference"),
	TYPE_MEAS_GEOM_OVERLAP("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#overlap"),
	TYPE_MEAS_GEOM_LENGTH_IN_POLYGON("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#lengthInPolygon"),
	
	//measurement - geometry / raster
	TYPE_MEAS_RASTER_ZONAL_STATS_MEAN("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#mean"),
	TYPE_MEAS_RASTER_ZONAL_STATS_MIN("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#min"),
	TYPE_MEAS_RASTER_ZONAL_STATS_MAX("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#max"),
	TYPE_MEAS_RASTER_ZONAL_STATS_STD("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#std"),
	
	//measurement - thematic
	TYPE_MEAS_STRING_DAMLEV("http://tu-dresden.de/uw/geo/gis/fusion/operation/thematic#damerauLevenshteinDistance"),
	
	//measurement - confidence
	TYPE_MEAS_CONF_BEST("http://tu-dresden.de/uw/geo/gis/fusion/operation/confidence#bestCorrespondence"),
	
	;
	
	/**
	 * resource object
	 */
	private IResource resource;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	private RDFVocabulary(String identifier){
		this.resource = new Resource(identifier);
	}
	
	/**
	 * get RDF resource
	 * @return RDF resource
	 */
	public IResource getResource(){
		return resource;
	}
	
	/**
	 * get RDF resource as string
	 * @returnRDF resource string
	 */
	public String getString(){
		return resource.getIdentifier();
	}
	
}
