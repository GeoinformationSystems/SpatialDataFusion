package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public enum Predicates implements IRDFVocabulary {

    /**
     * RDF predicates
     */

    TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
    VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value"),
    MEMBER("http://www.w3.org/1999/02/22-rdf-syntax-ns#li"),

    /**
     * GeoSPARQL predicates
     */

    GEOMETRY("http://www.opengis.net/ont/geosparql#hasGeometry"),
    DEFAULT_GEOMETRY("http://www.opengis.net/ont/geosparql#hasDefaultGeometry"),
    SERIALIZATION("http://www.opengis.net/ont/geosparql#hasSerialization"),
    asWKT("http://www.opengis.net/ont/geosparql#asWKT"),
    asGML("http://www.opengis.net/ont/geosparql#asGML"),

    /**
     *
     */

    FEATURE_VIEW("http://tu-dresden.de/uw/geo/gis/fusion/relation#featureView"),

    /**
     * Measurement
     */

    MEASUREMENT_VALUE_RANGE("http://tu-dresden.de/uw/geo/gis/fusion#hasRange"),
    HAS_RANGE_MEMBER("http://tu-dresden.de/uw/geo/gis/fusion#hasRangeMember"),
    RANGE_IS_CONTINUOUS("http://tu-dresden.de/uw/geo/gis/fusion#isContinuous"),
    MEASUREMENT_UOM("http://tu-dresden.de/uw/geo/gis/fusion/relation#uom"),
    MEASUREMENT_PROCESS("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurementProcess"),
    OPERATION("http://tu-dresden.de/uw/geo/gis/fusion/relation#operation"),

    /**
     * Measurements & Relations
     */

    HAS_DOMAIN("http://tu-dresden.de/uw/geo/gis/fusion/relation#domain"),
    HAS_RANGE("http://tu-dresden.de/uw/geo/gis/fusion/relation#range"),
    ROLE_OF_DOMAIN("http://tu-dresden.de/uw/geo/gis/fusion/relation#roleOfDomain"),
    ROLE_OF_RANGE("http://tu-dresden.de/uw/geo/gis/fusion/relation#roleOfRange"),
    RELATION_TYPE("http://tu-dresden.de/uw/geo/gis/fusion/relation#type"),;

    private IResource resource;

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    Predicates(String identifier) {
        this.resource = new Resource(new Identifier(URI.create(identifier)));
    }

    @NotNull
    @Override
    public IResource getResource() {
        return resource;
    }
}
