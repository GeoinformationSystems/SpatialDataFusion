package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

public enum Objects implements IRDFVocabulary {

    /**
     * RDF LiteralData types
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

    /**
     * OGC GeoSPARQL object types
     */

    FEATURE("http://www.opengis.net/ont/geosparql#Feature"),
    GEOMETRY("http://www.opengis.net/ont/geosparql#Geometry"),
    WKT_LITERAL("http://www.opengis.net/ont/geosparql#wktLiteral"),
    GML_LITERAL("http://www.opengis.net/ont/geosparql#gmlLiteral"),

    /**
     * W3C Geo Lat/Lon
     */

    W3C_GEO_LAT("http://www.w3.org/2003/01/geo/wgs84_pos#lat"),
    W3C_GEO_LON("http://www.w3.org/2003/01/geo/wgs84_pos#long"),

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
     * W3C time vocabulary
     */

    TIME_INTERVAL("http://www.w3.org/2006/time#interval"),
    TIME_INSTANT("http://www.w3.org/2006/time#instant"),

    /**
     * feature and feature views
     */

    COVERAGE("http://tu-dresden.de/uw/geo/gis/fusion#coverage"),
    FEATURE_CONCEPT("http://tu-dresden.de/uw/geo/gis/fusion#featureConcept"),
    FEATURE_TYPE("http://tu-dresden.de/uw/geo/gis/fusion#featureType"),
    FEATURE_ENTITY("http://tu-dresden.de/uw/geo/gis/fusion#featureInstance"),
    FEATURE_REPRESENTATION("http://tu-dresden.de/uw/geo/gis/fusion#featureRepresentation"),

    /**
     * relations
     */

    RELATION("http://tu-dresden.de/uw/geo/gis/fusion#relation"),
    BINARY_RELATION("http://tu-dresden.de/uw/geo/gis/fusion#binaryRelation"),
    RELATION_MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion#relationMeasurement"),
    RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion#relationType"),
    ROLE("http://tu-dresden.de/uw/geo/gis/fusion#role"),

    ;

    private IResource resource;

    /**
     * constructor
     * @param identifier resource identifier
     */
    Objects(String identifier){
        this.resource = new Resource(new Identifier(identifier));
    }

    @Override
    public IResource getResource() {
        return resource;
    }
}
