package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.net.URI;
import java.util.UUID;

/**
 * RDF identifier implementation
 */
public class Identifier implements IIdentifier {

    private final String identifier;
    private transient URI uri;

    /**
     * constructor
     * @param identifier identifier
     */
    public Identifier(String identifier){
        if(identifier == null)
            throw new IllegalArgumentException("identifier must not be null");
        this.identifier = identifier;
    }

    /**
     * constructor
     * @param uri identifier
     */
    public Identifier(URI uri){
        this(uri.toString());
    }

    /**
     * empty constructor, generates random identifier
     */
    public Identifier(){
        this(UUID.randomUUID().toString());
    }

    @Override
    public URI toURI() {
        if(uri == null)
            uri = URI.create(this.identifier);
        return uri;
    }

    @Override
    public String toString() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object identifier){
        if(identifier instanceof Identifier)
            return this.identifier.equals(identifier.toString());
        return false;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    /**
     * relativize resource URI
     * @param uriBase URI base (is omitted from result)
     * @return URI relative to uriBase
     */
    public URI relativizeURI(URI uriBase){
        if(uriBase == null)
            return toURI();
        return uriBase.relativize(toURI());
    }

}
