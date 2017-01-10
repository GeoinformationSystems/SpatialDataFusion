package de.tudresden.geoinfo.fusion.data.feature.osm;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.geometry.jts.GeometryBuilder;

import java.util.LinkedList;
import java.util.Set;

/**
 * Basic OSM way objective
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OSMWay extends OSMVectorFeature {

	/**
	 * constructor
	 * @param propertySet OSM way properties
	 * @param metadata OSM way description
	 * @param nodes associated OSM nodes
	 * @param relations associated relations
	 */
	public OSMWay(OSMPropertySet propertySet, IMetadataForData metadata, LinkedList<OSMNode> nodes, Set<IRelation<? extends IFeature>> relations) {
		super(propertySet, getWayGeometry(nodes), metadata, relations);
	}

    /**
     * get way geometry
     * @param nodes OSM nodes
     * @return feature geometry
     */
    private static Geometry getWayGeometry(LinkedList<OSMNode> nodes) {
        if(nodes == null || nodes.size() < 2)
            throw new IllegalArgumentException("OSMWay requires at least two nodes");
        //check for polygon
        boolean isPolygon = nodes.size() >= 4 && nodes.getFirst().equals(nodes.getLast());
        //create geometry
        return isPolygon ? new GeometryBuilder().polygon(getPointArray(nodes)) : new GeometryBuilder().lineString(getPointArray(nodes));
    }
	
	/**
	 * create point array
	 * @return point array
	 */
	private static double[] getPointArray(LinkedList<OSMNode> nodes){
		double[] array = new double[nodes.size() * 2];
		int i=0;
		for(OSMNode node : nodes){
			array[i++] = node.getLon();
			array[i++] = node.getLat();
		}
		return array;
	}

}
