package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public enum Relations implements IRDFVocabulary {

    /**
     * range - domain relation roles
     */
    ROLE_DOMAIN("http://tu-dresden.de/uw/geo/gis/fusion/relation/role#domain"),
    ROLE_RANGE("http://tu-dresden.de/uw/geo/gis/fusion/relation/role#range"),

    /**
     * OGC GeoSPARQL topological relations
     */

    SF_EQUALS("http://www.opengis.net/ont/geosparql#sfEquals"),
    SF_DISJOINT("http://www.opengis.net/ont/geosparql#sfDisjoint"),
    SF_INTERSECTS("http://www.opengis.net/ont/geosparql#sfIntersects"),
    SF_TOUCHES("http://www.opengis.net/ont/geosparql#sfTouches"),
    SF_WITHIN("http://www.opengis.net/ont/geosparql#sfWithin"),
    SF_CONTAINS("http://www.opengis.net/ont/geosparql#sfContains"),
    SF_OVERLAPS("http://www.opengis.net/ont/geosparql#sfOverlaps"),
    SF_CROSSES("http://www.opengis.net/ont/geosparql#sfCrosses"),

    /**
     * mereological relationships
     */
    MEREOLOGY_HASPART("https://www.w3.org/2001/sw/BestPractices/OEP/SimplePartWhole/part.owl#hasPart"),
    MEREOLOGY_PARTOF("https://www.w3.org/2001/sw/BestPractices/OEP/SimplePartWhole/part.owl#partOf"),;

    private IResource resource;

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    Relations(String identifier) {
        this.resource = new Resource(new Identifier(URI.create(identifier)));
    }

    @NotNull
    @Override
    public IResource getResource() {
        return resource;
    }
}
