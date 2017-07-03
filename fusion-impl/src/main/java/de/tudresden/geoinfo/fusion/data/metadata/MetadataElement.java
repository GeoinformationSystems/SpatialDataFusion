package de.tudresden.geoinfo.fusion.data.metadata;

import de.tudresden.geoinfo.fusion.data.IMetadataElement;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MetadataElement implements IMetadataElement {

    private IResource md_resource;
    private Object md_object;

    public MetadataElement(IResource md_resource, Object md_object) {
        this.md_resource = md_resource;
        this.md_object = md_object;
    }

    @Override
    public @NotNull IResource getResource() {
        return md_resource;
    }

    @Override
    public @NotNull Object getValue() {
        return md_object;
    }
}
