package de.tudresden.geoinfo.fusion.data.metadata;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFVocabulary;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Dublin Core metadata resources
 */
public enum DC_Metadata implements IRDFVocabulary {

    /**
     * general description
     */
    DC_TITLE("http://purl.org/dc/terms/title"),
    DC_ABSTRACT("http://purl.org/dc/terms/abstract"),

    /**
     * lineage
     */
    DC_SOURCE("http://purl.org/dc/terms/source");

    private IResource resource;

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    DC_Metadata(String identifier) {
        this.resource = new Resource(new Identifier(URI.create(identifier)));
    }

    @NotNull
    @Override
    public IResource getResource() {
        return resource;
    }

}
