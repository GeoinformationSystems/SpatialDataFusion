package de.tudresden.geoinfo.fusion.data.feature.osm;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.geometry.jts.GeometryBuilder;

import java.util.Set;

public class OSMNode extends OSMVectorFeature {
	
	public static final String OSM_PROPERTY_LAT = "lat";
    public static final String OSM_PROPERTY_LON = "lon";
	
	/**
	 * constructor
	 * @param propertySet OSM property set
	 * @param metadata OSM object description
	 * @param relations feature relations
	 */
	public OSMNode(OSMPropertySet propertySet, IMetadataForData metadata, Set<IRelation<? extends IFeature>> relations){
		super(propertySet, getNodeGeometry(propertySet), metadata, relations);
	}

    /**
     * get latitude
     * @return latitude
     */
    public double getLat() {
        return getLat(getPropertySet());
    }

    /**
     * get longitude
     * @return longitude
     */
    public double getLon() {
        return getLon(getPropertySet());
    }

    @Override
    public boolean equals(Object node){
        if(!(node instanceof OSMNode))
            return false;
        return ((OSMNode) node).getLat() == this.getLat() && ((OSMNode) node).getLon() == this.getLon();
    }

	/**
	 * get node geometry
	 * @param propertySet OSM property set
	 * @return feature geometry
	 */
	private static Geometry getNodeGeometry(OSMPropertySet propertySet) {
        if(!propertySet.containsKey(OSM_PROPERTY_LAT) || !propertySet.containsKey(OSM_PROPERTY_LON))
            throw new IllegalArgumentException("OSM Feature is not a valid OSM node");
		return new GeometryBuilder().point(getLon(propertySet), getLat(propertySet));
	}

    /**
     * get node latitude
     * @return latitude
     */
    static double getLat(OSMPropertySet propertySet) {
        return (Double) propertySet.getProperty(OSM_PROPERTY_LAT);
    }

    /**
     * get node longitude
     * @return longitude
     */
    static double getLon(OSMPropertySet propertySet) {
        return (Double) propertySet.getProperty(OSM_PROPERTY_LON);
    }
	
}
