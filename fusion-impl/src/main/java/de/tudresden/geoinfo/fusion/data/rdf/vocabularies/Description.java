package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public enum Description implements IRDFVocabulary {

    /**
     * Dublin Core
     */

    DC_DESCRIPTION("http://purl.org/dc/terms/description"),
    DC_TITLE("http://purl.org/dc/elements/1.1/title"),
    DC_ABSTRACT("http://purl.org/dc/terms/abstract"),

    /**
     * Darwin Core
     */

    DWC_OCCURRENCE("http://rs.tdwg.org/dwc/terms/Occurrence"),

    /**
     * Process Metadata
     */


    /**
     * Process Metadata - Measurements
     */

    MEASUREMENT_VALUE_RANGE("http://tu-dresden.de/uw/geo/gis/fusion#hasRange"),
    HAS_RANGE_MEMBER("http://tu-dresden.de/uw/geo/gis/fusion#hasRangeMember"),
    RANGE_IS_CONTINUOUS("http://tu-dresden.de/uw/geo/gis/fusion#isContinuous"),
    MEASUREMENT_UOM("http://tu-dresden.de/uw/geo/gis/fusion/relation#uom"),
    MEASUREMENT_PROCESS("http://tu-dresden.de/uw/geo/gis/fusion/relation#measurementProcess"),
    OPERATION("http://tu-dresden.de/uw/geo/gis/fusion/relation#operation"),;

    private IResource resource;

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    Description(String identifier) {
        this.resource = new Resource(new Identifier(URI.create(identifier)));
    }

    @NotNull
    @Override
    public IResource getResource() {
        return resource;
    }
}
