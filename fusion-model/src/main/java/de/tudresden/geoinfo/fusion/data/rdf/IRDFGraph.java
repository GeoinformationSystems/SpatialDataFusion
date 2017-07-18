package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Basic RDF graph
 */
public interface IRDFGraph extends IRDFResource {

    /**
     * get RDF subjects in this RDF graph
     * @return RDF subjects in the graph
     */
    @NotNull
    Collection<? extends IRDFSubject> getRDFSubjects();

}
