package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.RDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import org.jetbrains.annotations.NotNull;

public enum Objects implements IRDFVocabulary {

    /**
     * RDF LiteralData types
     */

    BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean", "boolean"),
    INTEGER("http://www.w3.org/2001/XMLSchema#integer", "int"),
    LONG("http://www.w3.org/2001/XMLSchema#long", "long"),
    DECIMAL("http://www.w3.org/2001/XMLSchema#decimal", "decimal"),
    STRING("http://www.w3.org/2001/XMLSchema#string", "string"),
    ANYURI("http://www.w3.org/2001/XMLSchema#anyURI", "URI"),

    /**
     * RDF collection types
     */

    BAG("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag", "rdfBag"),

    /**
     * OGC GeoSPARQL object types
     */

    FEATURE("http://www.opengis.net/ont/geosparql#Feature", "feature"),
    GEOMETRY("http://www.opengis.net/ont/geosparql#Geometry", "geometry"),
    WKT_LITERAL("http://www.opengis.net/ont/geosparql#wktLiteral", "wkt"),
    GML_LITERAL("http://www.opengis.net/ont/geosparql#gmlLiteral", "gml"),

    /**
     * GeoJSON Vocabulary
     */
    FEATURE_COLLECTION("https://purl.org/geojson/vocab#FeatureCollection", "featureCollection"),

    /**
     * SSN Vocabulary
     */
    OBSERVATION("http://purl.oclc.org/NET/ssnx/ssn#Observation", "observation"),

    /**
     * W3C Geo Lat/Lon
     */

    W3C_GEO_LAT("http://www.w3.org/2003/01/geo/wgs84_pos#lat", "lat"),
    W3C_GEO_LON("http://www.w3.org/2003/01/geo/wgs84_pos#long", "lon"),

    /**
     * OGC GML geometry types
     */

    GML3_0D_POINT("http://www.opengis.net/ont/gml#Point", "gml_point"),
    GML3_0D_MULTIPOINT("http://www.opengis.net/ont/gml#MultiPoint", "gml_multiPoint"),
    GML3_1D_CURVE("http://www.opengis.net/ont/gml#Curve", "gml_curve"),
    GML3_1D_MULTICURVE("http://www.opengis.net/ont/gml#MultiCurve", "gml_multiCurve"),
    GML3_1D_LINESTRING("http://www.opengis.net/ont/gml#LineString", "gml_linestring"),
    GML3_2D_SURFACE("http://www.opengis.net/ont/gml#Surface", "gml_surface"),
    GML3_2D_MULTISURFACE("http://www.opengis.net/ont/gml#MultiSurface", "gml_multiSurface"),
    GML3_2D_POLYGON("http://www.opengis.net/ont/gml#Polygon", "gml_polygon"),
    GML3_COVERAGE_RECTIFIED_GRID("http://schemas.opengis.net/gml/3.2.1/coverage.xsd#RectifiedGridCoverage", "gml_coverage"),
    GML3_GEOMETRY("http://www.opengis.net/ont/geosparql#Geometry", "gml_geometry"),
    GML3_MULTIGEOMETRY("http://www.opengis.net/ont/gml#MultiGeometry", "gml_multiGeometry"),

    /**
     * W3C time vocabulary
     */

    TIME_INTERVAL("http://www.w3.org/2006/time#interval", "timeInterval"),
    TIME_INSTANT("http://www.w3.org/2006/time#instant", "timeInstant"),

    /**
     * feature and feature views
     */

    COVERAGE("http://tu-dresden.de/uw/geo/gis/fusion#coverage", "coverage"),
    FEATURE_CONCEPT("http://tu-dresden.de/uw/geo/gis/fusion#featureConcept", "featureConcept"),
    FEATURE_TYPE("http://tu-dresden.de/uw/geo/gis/fusion#featureType", "featureType"),
    FEATURE_ENTITY("http://tu-dresden.de/uw/geo/gis/fusion#featureInstance", "featureInstance"),
    FEATURE_REPRESENTATION("http://tu-dresden.de/uw/geo/gis/fusion#featureRepresentation", "featureRepresentation"),

    /**
     * relations
     */

    RELATION("http://tu-dresden.de/uw/geo/gis/fusion#relation", "relation"),
    RELATION_COLLECTION("http://tu-dresden.de/uw/geo/gis/fusion#relationCollection", "relationCollection"),
    BINARY_RELATION("http://tu-dresden.de/uw/geo/gis/fusion#binaryRelation", "binaryRelation"),
    MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion#measurement", "measurement"),
    RELATION_MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion#relationMeasurement", "relationMeasurement"),
    RELATION_MEASUREMENT_COLLECTION("http://tu-dresden.de/uw/geo/gis/fusion#relationMeasurementCollection", "relationMeasurementCollection"),
    RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion#relationType", "relationType"),
    ROLE("http://tu-dresden.de/uw/geo/gis/fusion#relationRole", "relationRole"),;

    private RDFProperty identifier;

    /**
     * constructor
     *
     * @param globalIdentifier global resource identifier
     * @param localIdentifier local resource identifier
     */
    Objects(String globalIdentifier, String localIdentifier) {
        this.identifier = new RDFProperty(globalIdentifier, localIdentifier);
    }

    @NotNull
    public IIdentifier getIdentifier() {
        return this.identifier;
    }

    @NotNull
    public IRDFProperty getResource() {
        return this.identifier;
    }
}
