package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class RDFProperty extends ResourceIdentifier implements IRDFProperty {

    private Set<IRDFStatement> statements = new HashSet<>();

    /**
     * constructor
     * @param globalIdentifier global identifier
     * @param localIdentifier local identifier
     */
    public RDFProperty(@NotNull String globalIdentifier, @Nullable String localIdentifier) {
        super(globalIdentifier, localIdentifier);
    }

    /**
     * constructor
     * @param resource RDF predicate
     */
    public RDFProperty(@NotNull String resource) {
        this(resource, null);
    }

    @Override
    public @NotNull String getIRI() {
        //noinspection ConstantConditions
        return super.getIRI();
    }

    @Override
    public Set<IRDFStatement> getTriples() {
        return this.statements;
    }

    /**
     * add a connection for this predicate
     * @param statement RDF statement
     */
    public void addStatement(IRDFStatement statement){
        if(statement.getPredicate().equals(this))
            this.statements.add(statement);
    }

}
