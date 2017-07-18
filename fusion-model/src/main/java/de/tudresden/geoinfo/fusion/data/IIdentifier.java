package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface IIdentifier {

    /**
     * unique identifier for this object (immutable)
     *
     * @return global object identifier
     */
    @NotNull
    String getGlobalIdentifier();

    /**
     * local identifier for this object (subject to change depending on application context)
     *
     * @return local object identifier
     */
    @NotNull
    String getLocalIdentifier();

    /**
     * edit local identifier
     */
    void setLocalIdentifier(@NotNull String localIdentifier);

    /**
     * check for global object equality
     * @param object object to test
     * @return true, if both global identifiers are equal
     */
    boolean globallyEquals(IIdentifier object);

    /**
     * check for local object equality
     * @param object object to test
     * @return true, if both local identifiers are equal
     */
    boolean locallyEquals(IIdentifier object);

}
