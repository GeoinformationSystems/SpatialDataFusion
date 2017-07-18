package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.RDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import org.jetbrains.annotations.NotNull;

public enum Predicates implements IRDFVocabulary {

    /**
     * RDF predicates
     */

    TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "type"),
    VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value", "value"),
    MEMBER("http://www.w3.org/1999/02/22-rdf-syntax-ns#li", "list"),

    /**
     * GeoSPARQL predicates
     */

    GEOMETRY("http://www.opengis.net/ont/geosparql#hasGeometry", "geometry"),
    DEFAULT_GEOMETRY("http://www.opengis.net/ont/geosparql#hasDefaultGeometry", "default geometry"),
    SERIALIZATION("http://www.opengis.net/ont/geosparql#hasSerialization", "serialization"),
    asWKT("http://www.opengis.net/ont/geosparql#asWKT", "wkt"),
    asGML("http://www.opengis.net/ont/geosparql#asGML", "gml"),

    /**
     * Measurements & Relations
     */

    MEASUREMENT("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurement", "relationMeasurement"),
    HAS_DOMAIN("http://tu-dresden.de/uw/geo/gis/fusion/relation#domain", "domain"),
    HAS_RANGE("http://tu-dresden.de/uw/geo/gis/fusion/relation#range", "range"),
    ROLE_OF_DOMAIN("http://tu-dresden.de/uw/geo/gis/fusion/relation#roleOfDomain", "domain role"),
    ROLE_OF_RANGE("http://tu-dresden.de/uw/geo/gis/fusion/relation#roleOfRange", "range role"),
    RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#type", "relation type"),

    ;

    private RDFProperty identifier;

    /**
     * constructor
     *
     * @param globalIdentifier global resource identifier
     * @param localIdentifier local resource identifier
     */
    Predicates(String globalIdentifier, String localIdentifier) {
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
