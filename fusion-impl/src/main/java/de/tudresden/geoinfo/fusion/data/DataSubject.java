package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFNode;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFSubject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * abstract data object
 */
public class DataSubject<T> extends DataResource<T> implements IRDFSubject {

    private Map<IRDFProperty, Set<IRDFNode>> predicateToNode = new HashMap<>();
    private int degree = 0;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param object     data object
     * @param metadata   metadata object
     */
    public DataSubject(@NotNull IIdentifier identifier, @NotNull T object, @Nullable IMetadata metadata) {
        super(identifier, object, metadata);
    }

    @Override
    public @NotNull Set<IRDFProperty> getPredicates() {
        return this.predicateToNode.keySet();
    }

    @Override
    public @NotNull Set<IRDFNode> getObjects(@NotNull IRDFProperty predicate) {
        return this.predicateToNode.get(predicate);
    }

    /**
     * setRDFProperty node set into object map
     *
     * @param predicate input predicate
     * @param nodes input nodes
     */
    public void addObject(@NotNull IRDFProperty predicate, @NotNull Collection<? extends IRDFNode> nodes) {
        for (IRDFNode node : nodes) {
            this.put(predicate, node);
        }
    }

    /**
     * add named edge (predicate) to this subject
     * @param predicate predicate connected to this node
     */
    public void addObject(@NotNull IRDFProperty predicate, IRDFNode node){
        this.put(predicate, node);
    }

    /**
     * add node with associated resource
     * @param predicate predicate resource
     * @param node node
     */
    private void put(IRDFProperty predicate, IRDFNode node){
        if(this.predicateToNode.containsKey(predicate))
            this.predicateToNode.get(predicate).add(node);
        else {
            Set<IRDFNode> nodes = new HashSet<>();
            nodes.add(node);
            this.predicateToNode.put(predicate, nodes);
        }
        degree++;
    }

    @Override
    public int getDegree() {
        return this.degree;
    }

}
