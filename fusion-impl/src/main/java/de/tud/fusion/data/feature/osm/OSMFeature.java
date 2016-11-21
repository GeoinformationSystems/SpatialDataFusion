package de.tud.fusion.data.feature.osm;

import java.util.Map;
import java.util.Set;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.Geometry;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.feature.AbstractFeature;
import de.tud.fusion.data.feature.FeatureConceptView;
import de.tud.fusion.data.feature.FeatureEntityView;
import de.tud.fusion.data.feature.FeatureRepresentationView;
import de.tud.fusion.data.feature.FeatureTypeView;
import de.tud.fusion.data.relation.IFeatureRelation;

/**
 * Basic OSM object instance
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class OSMFeature extends AbstractFeature {
	
	public static final String OSM_GEOMETRY_ID = "geometry";
	
	/**
	 * constructor
	 * @param identifier feature identifier
	 * @param properties OSM object properties
	 * @param tags OSM object tags
	 * @param description OSM object description
	 */
	public OSMFeature(XMLPropertySet propertySet, IDataDescription description, Set<IFeatureRelation> relations) {
		super(propertySet.getIdentifier(), propertySet, description, relations);
	}
	
	@Override
	public XMLPropertySet resolve() { 
		return (XMLPropertySet) super.resolve(); 
	}
	
	@Override
	public boolean equals(Object node){
		return node instanceof OSMFeature ? getIdentifier().equals(((OSMFeature) node).getIdentifier()) : false;
	}
	
	@Override
	public FeatureRepresentationView initRepresentation() {
		SimpleFeatureType type = (SimpleFeatureType) getType().resolve();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
		for(Map.Entry<String,Object> property : resolve().getProperties().entrySet()){
			builder.set(property.getKey(), property.getValue());
		}
		for(Map.Entry<String,Object> tag : resolve().getTags().entrySet()){
			builder.set(tag.getKey(), tag.getValue());
		}
		builder.set(OSM_GEOMETRY_ID, getGeometry());
		return new FeatureRepresentationView(resolve().getIdentifier(), builder.buildFeature(resolve().getIdentifier()), null);
	}

	@Override
	public FeatureEntityView initEntity() {
		return new FeatureEntityView(resolve().getIdentifier(), resolve().getIdentifier(), null);
	}

	@Override
	public FeatureTypeView initType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("OSMFeatureType");
		builder.setSRS("EPSG:4326"); //default CRS for OSM
		builder.add(OSM_GEOMETRY_ID, Geometry.class, DefaultGeographicCRS.WGS84);
		for(Map.Entry<String,Object> property : resolve().getProperties().entrySet()){
			builder.add(property.getKey(), property.getValue().getClass());
		}
		for(Map.Entry<String,Object> tag : resolve().getTags().entrySet()){
			builder.add(tag.getKey(), tag.getValue().getClass());
		}
		return new FeatureTypeView(null, builder.buildFeatureType(), null);
	}

	@Override
	public FeatureConceptView initConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * get geometry of the OSM feature
	 * @return OSM feature geometry
	 */
	public abstract Geometry getGeometry();

}
