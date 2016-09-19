package de.tud.fusion.data.feature.osm;

import java.util.Set;

import org.geotools.geometry.jts.GeometryBuilder;

import com.vividsolutions.jts.geom.Geometry;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.relation.IFeatureRelation;

public class OSMNode extends OSMFeature {
	
	public static final String OSM_PROPERTY_LAT = "lat";
	public static final String OSM_PROPERTY_LON = "lon";
	
	/**
	 * constructor
	 * @param properties OSM object properties
	 * @param tags OSM object tags
	 * @param description OSM object description
	 */
	public OSMNode(XMLPropertySet propertySet, IDataDescription description, Set<IFeatureRelation> relations){
		super(propertySet, description, relations);
		if(!propertySet.getProperties().containsKey(OSM_PROPERTY_LAT) || !propertySet.getProperties().containsKey(OSM_PROPERTY_LON))
			throw new IllegalArgumentException("OSM Feature is not a valid OSM node");
	}

	/**
	 * get node latitude
	 * @return latitude
	 */
	public double getLat() { 
		return (Double) resolve().getProperties().get(OSM_PROPERTY_LAT); 
	}
	
	/**
	 * get node longitude
	 * @return longitude
	 */
	public double getLon() { 
		return (Double) resolve().getProperties().get(OSM_PROPERTY_LON); 
	}

	@Override
	public Geometry getGeometry() {
		return new GeometryBuilder().point(getLon(), getLat());
	}
	
}
