package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureType;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
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
     * @param identifier resource identifier
     * @param type        feature type object
     * @param description
     */
    public GTFeatureType(IIdentifier identifier, SimpleFeatureType type, IMetadataForData description) {
        super(identifier, type, description);
    }

    @Override
    public SimpleFeatureType resolve() {
        return (SimpleFeatureType) super.resolve();
    }

    @Override
    public Set<IIdentifier> getProperties() {
        if(properties == null){
            properties = new HashSet<>();
            for(AttributeDescriptor descriptor : resolve().getAttributeDescriptors()){
                properties.add(new Identifier(descriptor.getLocalName()));
            }
        }
        return properties;
    }
}
