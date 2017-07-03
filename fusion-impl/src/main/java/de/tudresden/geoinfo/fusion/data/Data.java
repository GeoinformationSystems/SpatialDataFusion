package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * abstract data object
 */
public class Data<T> extends Resource implements IData {

    private T object;
    private IMetadata metadata;

    /**
     * constructor
     *
     * @param identifier data identifier
     * @param object     data object
     * @param metadata   data metadata
     */
    public Data(@Nullable IIdentifier identifier, @NotNull T object, @Nullable IMetadata metadata) {
        super(identifier);
        this.setObject(object);
        this.setMetadata(metadata);
    }

    /**
     * constructor
     *
     * @param identifier data identifier
     * @param object     data object
     */
    public Data(@Nullable IIdentifier identifier, @NotNull T object) {
        this(identifier, object, null);
    }

    @NotNull
    @Override
    public T resolve() {
        return object;
    }

    /**
     * set object instance
     *
     * @param object input object
     */
    protected void setObject(T object) {
        this.object = object;
    }

    @NotNull
    @Override
    public IMetadata getMetadata() {
        return metadata;
    }

    /**
     * set object metadata
     *
     * @param metadata metadata object
     */
    protected void setMetadata(@Nullable IMetadata metadata) {
        this.metadata = metadata != null ? metadata : new Metadata();
    }

    /**
     * add a metadata element
     *
     * @param element metadata element
     */
    protected void addMetadataElement(IMetadataElement element) {
        this.metadata.addElement(element);
    }

}
