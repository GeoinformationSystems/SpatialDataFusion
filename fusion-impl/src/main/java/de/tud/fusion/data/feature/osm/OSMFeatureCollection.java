package de.tud.fusion.data.feature.osm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.feature.FeatureCollection;

public class OSMFeatureCollection<T extends OSMFeature> extends FeatureCollection<T> {
	
	private transient Set<OSMNode> nodes;
	private transient Set<OSMWay> ways;
	private transient Set<OSMRelation> relations;

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools GTFeature collection
	 * @param description collection description
	 */
	public OSMFeatureCollection(String identifier, Collection<T> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
	}
	
	/**
	 * get OSM nodes
	 * @return OSM nodes in the collection
	 */
	public OSMFeatureCollection<OSMNode> getNodes() {
		if(nodes == null){
			nodes = new HashSet<OSMNode>();
			for(OSMFeature feature : this.resolve()){
				if(feature instanceof OSMNode){
					nodes.add((OSMNode) feature); 
				}
			}
		}
		return new OSMFeatureCollection<OSMNode>(getIdentifier(), nodes, getDescription());
	}
	
	/**
	 * get OSM ways
	 * @return OSM ways in the collection
	 */
	public OSMFeatureCollection<OSMWay> getWays() {
		if(ways == null){
			ways = new HashSet<OSMWay>();
			for(OSMFeature feature : this.resolve()){
				if(feature instanceof OSMWay){
					ways.add((OSMWay) feature); 
				}
			}
		}
		return new OSMFeatureCollection<OSMWay>(getIdentifier(), ways, getDescription());
	}
	
	/**
	 * get OSM relations
	 * @return OSM relations in the collection
	 */
	public OSMFeatureCollection<OSMRelation> getRelations() {
		if(relations == null){
			relations = new HashSet<OSMRelation>();
			for(OSMFeature feature : this.resolve()){
				if(feature instanceof OSMRelation){
					relations.add((OSMRelation) feature); 
				}
			}
		}
		return new OSMFeatureCollection<OSMRelation>(getIdentifier(), relations, getDescription());
	}
	
	/**
	 * get bounds of the collection
	 * @return [minLat, maxLat, minLon, maxLon]
	 */
	public double[] getBounds() {
		double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
		for(OSMNode feature : nodes){
			if(minLat > feature.getLat())
				minLat = feature.getLat();
			if(maxLat < feature.getLat())
				maxLat = feature.getLat();
			if(minLon > feature.getLon())
				minLon = feature.getLon();
			if(maxLon < feature.getLon())
				maxLon = feature.getLon();
		}
		return new double[]{minLat, maxLat, minLon, maxLon};
	}

}
