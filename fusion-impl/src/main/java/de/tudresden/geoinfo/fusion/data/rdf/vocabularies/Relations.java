package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.RDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import org.jetbrains.annotations.NotNull;

public enum Relations implements IRDFVocabulary {

    /**
     * range & domain role of a relation
     */
    ROLE_DOMAIN("http://tu-dresden.de/uw/geo/gis/fusion/relation/role#domain", "domain"),
    ROLE_RANGE("http://tu-dresden.de/uw/geo/gis/fusion/relation/role#range", "range"),

    /**
     * OGC GeoSPARQL topological relations
     */

    SF_EQUALS("http://www.opengis.net/ont/geosparql#sfEquals", "equals"),
    SF_DISJOINT("http://www.opengis.net/ont/geosparql#sfDisjoint", "disjoint"),
    SF_INTERSECTS("http://www.opengis.net/ont/geosparql#sfIntersects", "intersects"),
    SF_TOUCHES("http://www.opengis.net/ont/geosparql#sfTouches", "touches"),
    SF_WITHIN("http://www.opengis.net/ont/geosparql#sfWithin", "within"),
    SF_CONTAINS("http://www.opengis.net/ont/geosparql#sfContains", "contains"),
    SF_OVERLAPS("http://www.opengis.net/ont/geosparql#sfOverlaps", "overlaps"),
    SF_CROSSES("http://www.opengis.net/ont/geosparql#sfCrosses", "crosses"),

    /**
     * mereological relationships
     */
    MEREOLOGY_HASPART("https://www.w3.org/2001/sw/BestPractices/OEP/SimplePartWhole/part.owl#hasPart", "hasPart"),
    MEREOLOGY_PARTOF("https://www.w3.org/2001/sw/BestPractices/OEP/SimplePartWhole/part.owl#partOf", "partOf"),

    /**
     * position of features in a directed linear graph
     */
    GRAPH_EQUALS("http://tu-dresden.de/uw/geo/gis/fusion/relation#globallyEquals", "equals"),
    GRAPH_DISJOINT("http://tu-dresden.de/uw/geo/gis/fusion/relation#disjoint", "disjoint"),
    GRAPH_DOWNWARDS("http://tu-dresden.de/uw/geo/gis/fusion/relation#downwards", "downwards"),
    GRAPH_UPWARDS("http://tu-dresden.de/uw/geo/gis/fusion/relation#upwards", "upwards"),

    ;

    private RDFProperty identifier;

    /**
     * constructor
     *
     * @param globalIdentifier global resource identifier
     * @param localIdentifier local resource identifier
     */
    Relations(String globalIdentifier, String localIdentifier) {
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
