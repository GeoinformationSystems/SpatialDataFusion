package de.tud.fusion.data.feature.osm;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import org.geotools.geometry.jts.GeometryBuilder;

import com.vividsolutions.jts.geom.Geometry;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.relation.IFeatureRelation;

/**
 * Basic OSM way objective
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OSMWay extends OSMFeature {

	private LinkedHashMap<String,OSMNode> nodeMap;
	private boolean isPolygon;
	
	/**
	 * constructor
	 * @param identifier OSM node identifier
	 * @param nodes associated OSM nodes
	 * @param attributes associated attributes
	 * @param relations associated relations
	 */
	public OSMWay(XMLPropertySet propertySet, IDataDescription description, LinkedList<OSMNode> nodes, Set<IFeatureRelation> relations) {
		super(propertySet, description, relations);
		setNodes(nodes);
	}
	
	/**
	 * set OSM attributes
	 * @param attributes
	 */
	private void setNodes(LinkedList<OSMNode> nodes) {
		if(nodes == null || nodes.size() < 2)
			throw new IllegalArgumentException("OSMWay requires at least two nodes");
		nodeMap = new LinkedHashMap<String,OSMNode>();
		for(OSMNode node : nodes){
			nodeMap.put(node.getIdentifier(), node);
		}
		isPolygon = nodes.size() > 2 && nodes.getFirst().equals(nodes.getLast());
	}

	/**
	 * get referenced OSM nodes
	 * @return referenced OSM nodes
	 */
	public LinkedHashMap<String,OSMNode> getNodes() {
		return nodeMap;
	}
	
	/**
	 * check if OSM way is a polygon
	 * @return true if polygon
	 */
	public boolean isPolygon(){
		return isPolygon;
	}

	@Override
	public Geometry getGeometry() {
		if(isPolygon)
			return new GeometryBuilder().polygon(getPointArray());
		else
			return new GeometryBuilder().lineString(getPointArray());
	}
	
	/**
	 * create point array
	 * @return point array
	 */
	private double[] getPointArray(){
		double[] array = new double[getNodes().size() * 2];
		int i=0;
		for(OSMNode node : nodeMap.values()){
			array[i++] = node.getLon();
			array[i++] = node.getLat();
		}
		return array;
	}

}
