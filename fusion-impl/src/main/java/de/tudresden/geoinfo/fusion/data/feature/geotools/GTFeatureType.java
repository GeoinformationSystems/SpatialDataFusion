package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.util.HashSet;
import java.util.Set;

/**
 * GeoTools feature type implementation
 */
public class GTFeatureType extends AbstractFeatureType<SimpleFeatureType> {

    private transient Set<String> properties;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param type       simple feature type
     */
    public GTFeatureType(@NotNull IIdentifier identifier, @NotNull SimpleFeatureType type, @Nullable IMetadata metadata) {
        super(identifier, type, metadata);
    }

    @NotNull
    @Override
    public Set<String> getProperties() {
        if (properties == null) {
            properties = new HashSet<>();
            for (AttributeDescriptor descriptor : resolve().getAttributeDescriptors()) {
                properties.add(descriptor.getLocalName());
            }
        }
        return properties;
    }
}
