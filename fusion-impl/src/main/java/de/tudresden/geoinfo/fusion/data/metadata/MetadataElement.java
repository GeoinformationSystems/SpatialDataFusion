package de.tudresden.geoinfo.fusion.data.metadata;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadataElement;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MetadataElement implements IMetadataElement {

    private IIdentifier md_identifier;
    private Object md_object;

    /**
     * constructor
     * @param md_identifier metadata element identifier
     * @param md_object metadata object
     */
    public MetadataElement(IIdentifier md_identifier, Object md_object) {
        this.md_identifier = md_identifier;
        this.md_object = md_object;
    }

    @Override
    public @NotNull IIdentifier getIdentifier() {
        return md_identifier;
    }

    @Override
    public @NotNull Object getValue() {
        return md_object;
    }
}
