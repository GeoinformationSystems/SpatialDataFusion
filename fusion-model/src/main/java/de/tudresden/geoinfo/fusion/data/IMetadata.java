package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Metadata
 * TODO support additional metadata elements
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
     * @param resource input resource
     * @return metadata element associated with input resource
     */
    @Nullable
    IMetadataElement getElement(IResource resource);

    /**
     * flag: metadata provides entry for input resource
     *
     * @param resource input resource
     * @return true, if metadata contains entry for input resource
     */
    boolean hasElement(IResource resource);

    /**
     * add a metadata element
     *
     * @param element input element
     */
    void addElement(@NotNull IMetadataElement element);

}
