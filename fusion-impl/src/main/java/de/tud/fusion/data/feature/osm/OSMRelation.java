package de.tud.fusion.data.feature.osm;

import java.util.Set;

import org.geotools.geometry.jts.GeometryBuilder;

import com.vividsolutions.jts.geom.Geometry;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.relation.IFeatureRelation;

public class OSMRelation extends OSMFeature {
	
	public static final String OSM_RELATION_MEMBER = "member";

	private Set<OSMRelationMember> members;
	
	/**
	 * constructor
	 * @param properties OSM relation properties
	 * @param tags OSM relation tags
	 * @param description OSM relation description
	 * @param member OSM relation member
	 */
	public OSMRelation(XMLPropertySet propertySet, IDataDescription description, Set<OSMRelationMember> members, Set<IFeatureRelation> relations) {
		super(propertySet, description, relations);
		setMembers(members);
	}
	
	/**
	 * set OSM attributes
	 * @param attributes
	 */
	private void setMembers(Set<OSMRelationMember> members) {
		if(members == null || members.size() < 2)
			throw new IllegalArgumentException("OSMRelation requires at least one member");
		this.members = members;
	}
	
	/**
	 * get relation members
	 * @return relation members
	 */
	public Set<OSMRelationMember> getMembers(){
		return members;
	}
	
	@Override
	public Geometry getGeometry() {
		Geometry[] geometries = new Geometry[members.size()];
		int i=0;
		for(OSMRelationMember member : members){
			geometries[i++] = member.getFeature().getGeometry();
		}
		return new GeometryBuilder().geometryCollection(geometries);
	}
	
	/**
	 * OSM relation member class
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	public static class OSMRelationMember {

		private OSMFeature feature;
		private String role;
		
		public OSMRelationMember(OSMFeature feature, String role){
			this.feature = feature;
			this.role = role;
		}
		
		public OSMFeature getFeature() {
			return feature;
		}
		
		public String getIdentifier() {
			return feature.getIdentifier();
		}
		
		public String getRole() {
			return role;
		}
		
	}
	
}
