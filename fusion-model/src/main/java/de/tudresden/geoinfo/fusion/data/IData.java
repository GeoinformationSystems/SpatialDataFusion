package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic data object
 */
public interface IData {

    /**
     * get identifier for this data object
     * @return data identifier
     */
    @NotNull IIdentifier getIdentifier();

    /**
     * resolve data object or value
     *
     * @return Java object represented by this resource
     */
    @NotNull
    Object resolve();

    /**
     * get metadata for the data element
     *
     * @return metadata element
     */
    @Nullable
    IMetadata getMetadata();

}
