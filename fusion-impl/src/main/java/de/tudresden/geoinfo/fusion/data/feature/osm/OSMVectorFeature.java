package de.tudresden.geoinfo.fusion.data.feature.osm;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.Map;
import java.util.Set;

/**
 * Basic OSM object instance
 */
public abstract class OSMVectorFeature extends GTVectorFeature {

    private static String OSM_GEOMETRY_ID = "geometry";
    private OSMPropertySet propertySet;

    /**
     * constructor
     *
     * @param propertySet OSM property set
     * @param geometry    OSM feature geometry
     * @param relations   feature relations
     */
    public OSMVectorFeature(@NotNull OSMPropertySet propertySet, @NotNull Geometry geometry, @Nullable IMetadata metadata, @Nullable Set<IRelation> relations) {
        super(propertySet.getIdentifier(), initSimpleFeature(propertySet, geometry), metadata, relations);
        this.propertySet = propertySet;
    }

    /**
     * initialize GeoTools feature
     *
     * @param propertySet OSM property set
     * @param geometry    OSM geometry
     * @return GeoTools feature implementation
     */
    @NotNull
    private static SimpleFeature initSimpleFeature(@NotNull OSMPropertySet propertySet, @NotNull Geometry geometry) {
        SimpleFeatureType type = initSimpleFeatureType(propertySet);
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
        for (Map.Entry<String, Object> property : propertySet.getProperties().entrySet()) {
            builder.set(property.getKey(), property.getValue());
        }
        for (Map.Entry<String, Object> tag : propertySet.getTags().entrySet()) {
            builder.set(tag.getKey(), tag.getValue());
        }
        builder.set(OSM_GEOMETRY_ID, geometry);
        return builder.buildFeature(propertySet.getGlobalIdentifier());
    }

    /**
     * initialize GeoTools feature type
     *
     * @param propertySet OSM property set
     * @return GeoTools feature type implementation
     */
    @NotNull
    private static SimpleFeatureType initSimpleFeatureType(@NotNull OSMPropertySet propertySet) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("OSMFeatureType");
        builder.setSRS("EPSG:4326"); //default CRS for OSM
        builder.add(OSM_GEOMETRY_ID, Geometry.class, DefaultGeographicCRS.WGS84);
        for (Map.Entry<String, Object> property : propertySet.getProperties().entrySet()) {
            builder.add(property.getKey(), property.getValue().getClass());
        }
        for (Map.Entry<String, Object> tag : propertySet.getTags().entrySet()) {
            builder.add(tag.getKey(), tag.getValue().getClass());
        }
        return builder.buildFeatureType();
    }

    @Override
    public boolean equals(@NotNull Object node) {
        return node instanceof OSMVectorFeature && this.getIdentifier().equals(((OSMVectorFeature) node).getIdentifier());
    }

    /**
     * get property set
     *
     * @return property set
     */
    @NotNull OSMPropertySet getPropertySet() {
        return propertySet;
    }

}
