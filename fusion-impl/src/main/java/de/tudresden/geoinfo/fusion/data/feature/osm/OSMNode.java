package de.tudresden.geoinfo.fusion.data.feature.osm;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.geotools.geometry.jts.GeometryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class OSMNode extends OSMVectorFeature {

    public static final String OSM_PROPERTY_LAT = "lat";
    public static final String OSM_PROPERTY_LON = "lon";

    /**
     * constructor
     *
     * @param propertySet OSM property set
     * @param relations   feature relations
     */
    public OSMNode(@NotNull OSMPropertySet propertySet, @Nullable Set<IRelation<? extends IFeature>> relations) {
        super(propertySet, getNodeGeometry(propertySet), null, relations);
    }

    /**
     * get node geometry
     *
     * @param propertySet OSM property set
     * @return feature geometry
     */
    @NotNull
    private static Geometry getNodeGeometry(@NotNull OSMPropertySet propertySet) {
        if (!propertySet.containsKey(OSM_PROPERTY_LAT) || !propertySet.containsKey(OSM_PROPERTY_LON))
            throw new IllegalArgumentException("OSM Feature is not a valid OSM node");
        return new GeometryBuilder().point(getLon(propertySet), getLat(propertySet));
    }

    /**
     * get node latitude
     *
     * @return latitude
     */
    private static double getLat(@NotNull OSMPropertySet propertySet) {
        return (Double) propertySet.getProperty(OSM_PROPERTY_LAT);
    }

    /**
     * get node longitude
     *
     * @return longitude
     */
    private static double getLon(@NotNull OSMPropertySet propertySet) {
        return (Double) propertySet.getProperty(OSM_PROPERTY_LON);
    }

    /**
     * get latitude
     *
     * @return latitude
     */
    public double getLat() {
        return getLat(getPropertySet());
    }

    /**
     * get longitude
     *
     * @return longitude
     */
    public double getLon() {
        return getLon(getPropertySet());
    }

    @Override
    public boolean equals(@NotNull Object node) {
        return node instanceof OSMNode && ((OSMNode) node).getLat() == this.getLat() && ((OSMNode) node).getLon() == this.getLon();
    }

}
