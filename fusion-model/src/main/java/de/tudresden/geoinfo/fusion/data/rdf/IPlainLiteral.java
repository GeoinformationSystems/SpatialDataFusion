package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.Nullable;

/**
 * RDF Plain Literal, literal with associated language identifier
 */
public interface IPlainLiteral extends ILiteral {

    /**
     * get language identifier according to IANA Language Subtag Registry
     *
     * @return language identifier
     */
    @Nullable
    IResource getLanguage();

}
