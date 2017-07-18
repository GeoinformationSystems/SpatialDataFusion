package de.tudresden.geoinfo.fusion.data.rdf;

/**
 *
 */
public interface IRDFStatement {

    /**
     * get RDF Subject for this RDF statement
     * @return RDF subject
     */
    IRDFSubject getSubject();

    /**
     * get RDF predicate for this RDF statement
     * @return RDF predicate
     */
    IRDFProperty getPredicate();

    /**
     * get RDF object for this RDF statement
     * @return RDF object
     */
    IRDFNode getObject();

}
