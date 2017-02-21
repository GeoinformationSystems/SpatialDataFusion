package de.tudresden.geoinfo.fusion.data.feature.osm;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import org.geotools.geometry.jts.GeometryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Set;

/**
 * Basic OSM way objective
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class OSMWay extends OSMVectorFeature {

    /**
     * constructor
     *
     * @param propertySet OSM way properties
     * @param nodes       associated OSM nodes
     * @param relations   associated relations
     */
    public OSMWay(@NotNull OSMPropertySet propertySet, @NotNull LinkedList<OSMNode> nodes, @Nullable Set<IRelation<? extends IFeature>> relations) {
        super(propertySet, getWayGeometry(nodes), null, relations);
    }

    /**
     * get way geometry
     *
     * @param nodes OSM nodes
     * @return feature geometry
     */
    @NotNull
    private static Geometry getWayGeometry(@NotNull LinkedList<OSMNode> nodes) {
        if (nodes.size() < 2)
            throw new IllegalArgumentException("OSMWay requires at least two nodes");
        //check for polygon
        boolean isPolygon = nodes.size() >= 4 && nodes.getFirst().equals(nodes.getLast());
        //create geometry
        return isPolygon ? new GeometryBuilder().polygon(getPointArray(nodes)) : new GeometryBuilder().lineString(getPointArray(nodes));
    }

    /**
     * create point array
     *
     * @return point array
     */
    private static double[] getPointArray(@NotNull LinkedList<OSMNode> nodes) {
        double[] array = new double[nodes.size() * 2];
        int i = 0;
        for (OSMNode node : nodes) {
            array[i++] = node.getLon();
            array[i++] = node.getLat();
        }
        return array;
    }

}
