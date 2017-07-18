package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFGraph;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFSubject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * graph object implementation
 */
public class DataCollection<T extends IData> extends HashSet<T> implements IDataCollection<T>,IRDFGraph {

    private ResourceIdentifier identifier;
    private IMetadata metadata;
    private Collection<IRDFSubject> rdfSubjects = new HashSet<>();
    private Map<String,T> localIdMap;
    private Map<String,T> globalIdMap;

    /**
     * constructor
     *
     * @param identifier collection identifier
     * @param collection   collection elements
     * @param metadata   collection metadata
     */
    public DataCollection(@NotNull IIdentifier identifier, @NotNull Collection<T> collection, @Nullable IMetadata metadata) {
        super();
        this.addAll(collection);
        this.identifier = new ResourceIdentifier(identifier);
        this.metadata = metadata;
    }

    /**
     * create collection index for identifiers
     */
    private void createIndex(){
        this.globalIdMap = new HashMap<>();
        this.localIdMap = new HashMap<>();
        for(T data : this) {
            this.globalIdMap.put(data.getIdentifier().getGlobalIdentifier(), data);
            this.localIdMap.put(data.getIdentifier().getLocalIdentifier(), data);
        }
    }

    @Override
    public boolean add(T data){
        if(data instanceof IRDFSubject)
            this.rdfSubjects.add((IRDFSubject) data);
        return super.add(data);
    }

    @Override
    public @NotNull IIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public @NotNull Collection<T> resolve() {
        return this;
    }

    @Override
    public @Nullable IMetadata getMetadata() {
        return this.metadata;
    }

    /**
     * get data object by identifier
     * @param identifier data identifier
     * @return data object or null, if no object with specified identifier exists
     */
    public @Nullable T getMember(IIdentifier identifier){
        return getMember(identifier.getGlobalIdentifier());
    }

    /**
     * get data object by local or global identifier
     * @param identifier data identifier
     * @return data object or null, if no object with specified identifier exists
     */
    public @Nullable T getMember(String identifier){
        if(this.globalIdMap == null || this.globalIdMap.size() != this.size())
            this.createIndex();
        if(this.globalIdMap.containsKey(identifier))
            return this.globalIdMap.get(identifier);
        else
            return this.localIdMap.get(identifier);
    }

    @Override
    public @NotNull Collection<? extends IRDFSubject> getRDFSubjects() {
        return this.rdfSubjects;
    }

    @Override
    public @Nullable String getIRI() {
        return this.identifier.getIRI();
    }

    @Override
    public boolean isBlank() {
        return this.identifier.isBlank();
    }

    @Override
    public @Nullable URI toURI() throws URISyntaxException {
        return this.identifier.toURI();
    }

    @Override
    public @Nullable URL toURL() throws MalformedURLException {
        return this.identifier.toURL();
    }
}
