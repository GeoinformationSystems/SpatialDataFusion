package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * RDF Literal, literal object in the RDF graph (can only be object of a triple)
 */
public interface IRDFLiteral extends IRDFNode {

    /**
     * get value of this literal node
     *
     * @return literal node value
     */
    @NotNull
    String getLiteralValue();

    /**
     * get literal type identifier
     *
     * @return literal type identifier
     */
    @NotNull
    IRDFProperty getLiteralType();

    /**
     * get language identifier according to IANA Language Subtag Registry
     * returns null, if RDF literal is not of type http://www.w3.org/1999/02/22-rdf-syntax-ns#langString
     *
     * @return language identifier
     */
    @Nullable
    String getLanguage();

}
