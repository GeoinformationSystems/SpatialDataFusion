package de.tudresden.geoinfo.fusion.data.metadata;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;

/**
 * Dublin Core metadata resources
 */
public enum MetadataVocabulary {

    /**
     * Dublin Core
     */
    DC_TITLE("http://purl.org/dc/terms/title", "title"),
    DC_ABSTRACT("http://purl.org/dc/terms/abstract", "abstract"),
    DC_SOURCE("http://purl.org/dc/terms/source", "source"),

    /**
     * QUDT units of measurement
     */

    DEGREE_ANGLE("http://qudt.org/vocab/unit#DegreeAngle", "degree"),
    RADIAN_ANGLE("http://qudt.org/vocab/unit#Radian", "radians"),
    KILOMETER("http://qudt.org/vocab/unit#Kilometer", "kilometer"),
    MILLISECOND("http://qudt.org/vocab/unit#MilliSecond", "millisecond"),
    PERCENT("http://qudt.org/vocab/unit#Percent", "percent"),
    NUMBER("http://qudt.org/vocab/unit#Number", "number"),

    /**
     * additional uom specifications
     */

    UNDEFINED("http://tu-dresden.de/uw/geo/gis/fusion/uom#undefined", "undefined"),
    UNKNOWN("http://tu-dresden.de/uw/geo/gis/fusion/uom#unknown", "unknown"),
    MAP_UNITS("http://tu-dresden.de/uw/geo/gis/fusion/uom#mapUnits", "map units"),

    /**
     * measurement metadata
     */

    MEASUREMENT_VALUE_RANGE("http://tu-dresden.de/uw/geo/gis/fusion#hasRange", "range"),
    MEASUREMENT_UOM("http://tu-dresden.de/uw/geo/gis/fusion/relation#uom", "uom"),
    MEASUREMENT_OPERATION("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurementOpreation", "operation"),

    ;

    private ResourceIdentifier identifier;

    /**
     * constructor
     *
     * @param globalIdentifier global resource identifier
     * @param localIdentifier local resource identifier
     */
    MetadataVocabulary(String globalIdentifier, String localIdentifier) {
        this.identifier = new ResourceIdentifier(globalIdentifier, localIdentifier);
    }

    @NotNull
    public IIdentifier getIdentifier() {
        return this.identifier;
    }

    @NotNull
    public IRDFResource getResource() {
        return this.identifier;
    }

}
