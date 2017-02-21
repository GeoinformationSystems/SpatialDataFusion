package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.INode;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.ISubject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * RDF subject implementation
 */
public class Subject extends Resource implements ISubject {

    private HashMap<IResource, Set<INode>> objectSet;

    /**
     * constructor, calls initializeObjectSet() to initialize associated object set
     *
     * @param identifier resource identifier
     */
    public Subject(@Nullable IIdentifier identifier) {
        super(identifier);
        resetObjectSet();
    }

    /**
     * initialize object set associated to subject
     *
     * @return associated object set
     */
    @NotNull
    protected HashMap<IResource, Set<INode>> initializeObjectSet() {
        return new HashMap<>();
    }

    /**
     * get object set associated to subject
     *
     * @return object set
     */
    @NotNull
    private HashMap<IResource, Set<INode>> getObjectSet() {
        return this.objectSet;
    }

    /**
     * reset object set (reset objectSet with value returned by initializeObjectSet())
     */
    protected void resetObjectSet() {
        this.objectSet = initializeObjectSet();
    }

    @NotNull
    @Override
    public Set<IResource> getPredicates() {
        return this.getObjectSet().keySet();
    }

    @Nullable
    @Override
    public Set<INode> getObjects(@NotNull IResource predicate) {
        return this.getObjectSet().get(predicate);
    }

    /**
     * get single object from object set
     * if multiple objects exist, only the first object is returned
     *
     * @param predicate input predicate
     * @return single object
     */
    @Nullable
    public INode getObject(@NotNull IResource predicate) {
        if (!this.getObjectSet().containsKey(predicate))
            return null;
        Set<INode> nodeSet = this.getObjectSet().get(predicate);
        return nodeSet.isEmpty() ? null : nodeSet.iterator().next();
    }

    /**
     * put single INode object to object map
     *
     * @param predicate input predicate
     * @param object    input object
     */
    protected void put(@NotNull IResource predicate, @NotNull INode object) {
        if (this.getObjectSet().containsKey(predicate))
            this.getObjectSet().get(predicate).add(object);
        else {
            Set<INode> set = new HashSet<>(Collections.singletonList(object));
            this.getObjectSet().put(predicate, set);
        }
    }

    /**
     * put node set into object map
     *
     * @param predicate input predicate
     * @param objectSet input node set
     */
    protected void put(@NotNull IResource predicate, @NotNull Collection<INode> objectSet) {
        if (objectSet.isEmpty())
            return;
        for (INode object : objectSet) {
            this.put(predicate, object);
        }
    }

    /**
     * drop predicate and associated objects from object set
     *
     * @param predicate input predicate to be removed
     * @return object set removed from the subject or null, if predicate is not linked to subject
     */
    protected Collection<INode> remove(@NotNull IResource predicate) {
        return this.getObjectSet().remove(predicate);
    }

    /**
     * get total number of objects related to subject
     *
     * @return number of objects
     */
    public int getNumberOfObjects() {
        int i = 0;
        for (Set<INode> set : this.getObjectSet().values()) {
            i += set.size();
        }
        return i;
    }

}
