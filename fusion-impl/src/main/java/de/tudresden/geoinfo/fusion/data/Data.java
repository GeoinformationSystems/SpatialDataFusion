package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * abstract data object
 */
public class Data<T> implements IData {

    private IIdentifier identifier;
    private T object;
    private IMetadata metadata;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param object     data object
     * @param metadata   metadata object
     */
    public Data(@NotNull IIdentifier identifier, @NotNull T object, @Nullable IMetadata metadata) {
        this.identifier = identifier;
        this.setObject(object);
        this.setMetadata(metadata);
    }

    @Override
    @NotNull
    public IIdentifier getIdentifier() {
        return this.identifier;
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

    /**
     * add a metadata element
     *
     * @param identifier metadata identifier
     */
    public @Nullable Object getMetadataObject(IIdentifier identifier) {
        IMetadataElement md_element = this.metadata.getElement(identifier);
        return md_element != null ? md_element.getValue() : null;
    }

}
