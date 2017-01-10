package de.tudresden.geoinfo.fusion.data.rdf.vocabularies;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

public enum Operations implements IRDFVocabulary {

    /**
     * operation object & description
     */

    OPERATION("http://tu-dresden.de/uw/geo/gis/fusion/operation"),
    OPERATION_DESCRIPTION("http://tu-dresden.de/uw/geo/gis/fusion/operation/description"),

    /**
     * measurement - topology
     */
    TOPOLOGY_DE9IM("http://tu-dresden.de/uw/geo/gis/fusion/operation/topology#de9im"),

    /**
     * measurement - geometry
     */
    GEOMETRY_DISTANCE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#distance"),
    GEOMETRY_DISTANCE_HAUSDORFF("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#hausdorffDistance"),
    GEOMETRY_DIFFERENCE_LENGTH("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#lengthDifference"),
    GEOMETRY_DIFFERENCE_ANGLE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#angleDifference"),
    GEOMETRY_DIFFERENCE_SINUOSITY("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#sinuosityDifference"),
    GEOMETRY_OVERLAP("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#overlap"),
    GEOMETRY_BBOX_DISTANCE("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#bboxOverlap"),
    GEOMETRY_INTERSECTION_LENGTH("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#lengthInPolygon"),

    /**
     * measurement - zonal statistics
     */
    ZONAL_STATISTICS_MEAN("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#mean"),
    ZONAL_STATISTICS_MIN("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#min"),
    ZONAL_STATISTICS_MAX("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#max"),
    ZONAL_STATISTICS_SDEV("http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#std"),

    /**
     * measurement - string
     */
    STRING_DAMERAU_LEVENSHTEIN("http://tu-dresden.de/uw/geo/gis/fusion/operation/thematic#damerauLevenshteinDistance"),

    /**
     * mapping - best correspondence
     */
    CONF_BEST("http://tu-dresden.de/uw/geo/gis/fusion/operation/confidence#bestCorrespondence"),

    ;

    private IResource resource;

    /**
     * constructor
     * @param identifier resource identifier
     */
    Operations(String identifier){
        this.resource = new Resource(new Identifier(identifier));
    }

    @Override
    public IResource getResource() {
        return resource;
    }
}
