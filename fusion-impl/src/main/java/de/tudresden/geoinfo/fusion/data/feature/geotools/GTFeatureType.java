package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureType;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.util.HashSet;
import java.util.Set;

/**
 * GeoTools feature type implementation
 */
public class GTFeatureType extends AbstractFeatureType {

    private transient Set<IIdentifier> properties;

    /**
     * constructor
     *
     * @param identifier type identifier
     * @param type       simple feature type
     */
    public GTFeatureType(@Nullable IIdentifier identifier, @NotNull SimpleFeatureType type, @Nullable IMetadata metadata) {
        super(identifier, type, metadata);
    }

    @NotNull
    @Override
    public SimpleFeatureType resolve() {
        return (SimpleFeatureType) super.resolve();
    }

    @NotNull
    @Override
    public Set<IIdentifier> getProperties() {
        if (properties == null) {
            properties = new HashSet<>();
            for (AttributeDescriptor descriptor : resolve().getAttributeDescriptors()) {
                properties.add(new Identifier(descriptor.getLocalName()));
            }
        }
        return properties;
    }
}
