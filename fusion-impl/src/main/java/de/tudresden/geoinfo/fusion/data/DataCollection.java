package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

/**
 * graph object implementation
 */
public class DataCollection<T extends IData> extends Data<Collection<T>> implements IDataCollection<T> {

    /**
     * constructor
     *
     * @param identifier collection identifier
     * @param elements   collection elements
     * @param metadata   collection metadata
     */
    public DataCollection(@Nullable IIdentifier identifier, @NotNull Collection<T> elements, @Nullable IMetadata metadata) {
        super(identifier, elements, metadata);
    }

    /**
     * constructor
     *
     * @param identifier  collection identifier
     * @param elements    collection elements
     * @param title       collection title
     * @param description collection description
     */
    public DataCollection(@Nullable IIdentifier identifier, @NotNull Collection<T> elements, @NotNull String title, @Nullable String description) {
        this(identifier, elements, new Metadata(title, description));
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.resolve().iterator();
    }

}
