package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public enum Units implements IRDFVocabulary {

    /**
     * predicate
     */

    UNIT("http://qudt.org/schema/qudt#unit"),

    /**
     * QUDT units of measurement
     */

    DEGREE_ANGLE("http://qudt.org/vocab/unit#DegreeAngle"),
    RADIAN_ANGLE("http://qudt.org/vocab/unit#Radian"),
    KILOMETER("http://qudt.org/vocab/unit#Kilometer"),
    MILLISECOND("http://qudt.org/vocab/unit#MilliSecond"),
    PERCENT("http://qudt.org/vocab/unit#Percent"),
    NUMBER("http://qudt.org/vocab/unit#Number"),

    /**
     * additional uom specifications
     */

    UNDEFINED("http://tu-dresden.de/uw/geo/gis/fusion/uom#undefined"),
    UNKNOWN("http://tu-dresden.de/uw/geo/gis/fusion/uom#unknown"),
    MAP_UNITS("http://tu-dresden.de/uw/geo/gis/fusion/uom#mapUnits"),;

    private IResource resource;

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    Units(String identifier) {
        this.resource = new Resource(new Identifier(URI.create(identifier)));
    }

    @NotNull
    @Override
    public IResource getResource() {
        return resource;
    }
}
