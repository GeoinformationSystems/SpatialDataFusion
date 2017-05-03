package de.tudresden.geoinfo.fusion.data;

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
        super(identifier, metadata != null ? metadata.getTitle() : null, metadata != null ? metadata.getDescription() : null);
        this.setObject(object);
        this.setMetadata(metadata);
    }

    /**
     * constructor
     *
     * @param identifier  data identifier
     * @param object      data object
     * @param title       data title
     * @param description data description
     */
    public Data(@Nullable IIdentifier identifier, @NotNull T object, @NotNull String title, @Nullable String description) {
        this(identifier, object, new Metadata(title, description));
    }

    @NotNull
    @Override
    public T resolve() {
        return object;
    }

    /**
     * set object instance
     * @param object input object
     */
    protected void setObject(T object){
        this.object = object;
    }

    @Nullable
    @Override
    public IMetadata getMetadata() {
        return metadata;
    }

    /**
     * set object metadata
     * @param metadata metadata object
     */
    protected void setMetadata(IMetadata metadata){
        this.metadata = metadata;
    }

}
