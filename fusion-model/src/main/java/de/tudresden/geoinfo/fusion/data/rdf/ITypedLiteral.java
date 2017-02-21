package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

/**
 * RDF Typed Literal, literal with associated type identifier
 */
public interface ITypedLiteral<T> extends ILiteral {

    /**
     * get literal type identifier
     *
     * @return literal type identifier
     */
    @NotNull
    IResource getLiteralType();

    /**
     * get literal value
     *
     * @return literal value
     */
    @NotNull
    T resolve();

}
