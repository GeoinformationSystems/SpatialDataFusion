package de.tudresden.geoinfo.fusion.data.feature.osm;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
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
	 * @param propertySet OSM property set
     * @param geometry OSM feature geometry
	 * @param metadata OSM object description
	 * @param relations feature relations
	 */
	public OSMVectorFeature(OSMPropertySet propertySet, Geometry geometry, IMetadataForData metadata, Set<IRelation<? extends IFeature>> relations) {
		super(propertySet.getIdentifier(), initSimpleFeature(propertySet, geometry), metadata, relations);
		this.propertySet = propertySet;
	}
	
	@Override
	public boolean equals(Object node){
		return node instanceof OSMVectorFeature && getIdentifier().equals(((OSMVectorFeature) node).getIdentifier());
	}

    /**
     * get property set
     * @return property set
     */
    protected OSMPropertySet getPropertySet() {
        return propertySet;
    }

    /**
     * initialize GeoTools feature
     * @param propertySet OSM property set
     * @param geometry OSM geometry
     * @return GeoTools feature implementation
     */
    protected static SimpleFeature initSimpleFeature(OSMPropertySet propertySet, Geometry geometry) {
        SimpleFeatureType type = initSimpleFeatureType(propertySet);
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
        for(Map.Entry<String,Object> property : propertySet.getProperties().entrySet()){
            builder.set(property.getKey(), property.getValue());
        }
        for(Map.Entry<String,Object> tag : propertySet.getTags().entrySet()){
            builder.set(tag.getKey(), tag.getValue());
        }
        builder.set(OSM_GEOMETRY_ID, geometry);
        return builder.buildFeature(propertySet.getIdentifier().toString());
    }

    /**
     * initialize GeoTools feature type
     * @param propertySet OSM property set
     * @return GeoTools feature type implementation
     */
    protected static SimpleFeatureType initSimpleFeatureType(OSMPropertySet propertySet) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("OSMFeatureType");
        builder.setSRS("EPSG:4326"); //default CRS for OSM
        builder.add(OSM_GEOMETRY_ID, Geometry.class, DefaultGeographicCRS.WGS84);
        for(Map.Entry<String,Object> property : propertySet.getProperties().entrySet()){
            builder.add(property.getKey(), property.getValue().getClass());
        }
        for(Map.Entry<String,Object> tag : propertySet.getTags().entrySet()){
            builder.add(tag.getKey(), tag.getValue().getClass());
        }
        return builder.buildFeatureType();
    }

}
