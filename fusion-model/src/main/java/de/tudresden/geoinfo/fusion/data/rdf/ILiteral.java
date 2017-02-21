package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

/**
 * RDF Literal, literal object in the RDF graph (can only be object of a triple)
 */
public interface ILiteral extends INode {

    /**
     * get value of this literal node
     *
     * @return literal node value
     */
    @NotNull
    String getLiteral();

}
