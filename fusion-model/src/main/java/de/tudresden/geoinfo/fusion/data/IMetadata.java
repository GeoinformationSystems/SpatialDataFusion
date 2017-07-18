package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Metadata
 */
public interface IMetadata {

    /**
     * get associated metadata elements
     *
     * @return metadata elements
     */
    @NotNull
    Collection<IMetadataElement> getElements();

    /**
     * get metadata element associated with input resource
     *
     * @param identifier element identifier
     * @return metadata element associated with input resource
     */
    @Nullable
    IMetadataElement getElement(IIdentifier identifier);

    /**
     * flag: metadata provides entry for input resource
     *
     * @param identifier element identifier
     * @return true, if metadata contains entry for input identifier
     */
    boolean hasElement(IIdentifier identifier);

    /**
     * add a metadata element
     *
     * @param element input element
     */
    void addElement(@NotNull IMetadataElement element);

}
