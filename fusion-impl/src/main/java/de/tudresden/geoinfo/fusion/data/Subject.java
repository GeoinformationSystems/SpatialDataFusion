package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.INode;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.ISubject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * RDF subject implementation
 */
public class Subject extends Resource implements ISubject {

    private HashMap<IResource, Set<INode>> objectSet = new HashMap<>();

    /**
     * constructor, calls initializeObjectSet() to initialize associated object set
     *
     * @param identifier resource identifier
     */
    public Subject(@Nullable IIdentifier identifier) {
        super(identifier);
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
     * setRDFProperty single INode object to object map
     *
     * @param predicate input predicate
     * @param object    input object
     */
    public void put(@NotNull IResource predicate, @NotNull INode object) {
        if (this.getObjectSet().containsKey(predicate))
            this.getObjectSet().get(predicate).add(object);
        else {
            Set<INode> set = new HashSet<>();
            set.add(object);
            this.getObjectSet().put(predicate, set);
        }
    }

    /**
     * setRDFProperty node set into object map
     *
     * @param predicate input predicate
     * @param objectSet input node set
     */
    public void put(@NotNull IResource predicate, @NotNull Collection<? extends INode> objectSet) {
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
