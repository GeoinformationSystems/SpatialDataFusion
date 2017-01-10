package de.tudresden.geoinfo.fusion.data.rdf;

import java.net.URI;

/**
 * RDF identifier
 */
public interface IIdentifier {

    /**
     * get URI representation for this identifier
     * @return URI representation
     */
    URI toURI();

}
