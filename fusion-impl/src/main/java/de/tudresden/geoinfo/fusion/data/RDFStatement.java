package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFNode;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFStatement;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFSubject;
import org.jetbrains.annotations.NotNull;

/**
 * RDF statement (triple) implementation
 */
public class RDFStatement implements IRDFStatement {

    private IRDFSubject subject;
    private IRDFNode object;
    private IRDFProperty predicate;

    /**
     * constructor
     * @param subject statement subject
     * @param predicate statement predicate
     * @param object statement object
     */
    public RDFStatement(@NotNull IRDFSubject subject, @NotNull IRDFProperty predicate, @NotNull IRDFNode object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public IRDFSubject getSubject(){
        return this.subject;
    }

    @Override
    public IRDFProperty getPredicate(){
        return this.predicate;
    }

    @Override
    public IRDFNode getObject(){
        return this.object;
    }

}
