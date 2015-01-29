package de.tudresden.gis.fusion.data.geotools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.metadata.IFeatureDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IResource;

public class GTFeature implements IIdentifiableResource,IFeature {
	
	private SimpleFeature feature;
	private IIRI iri;
	private IFeatureDescription description;
	
	private Collection<ISpatialProperty> spatialProperties;
	private Collection<IThematicProperty> thematicProperties;
	
	public GTFeature(IIRI iri, SimpleFeature feature, IFeatureDescription description){
		this.iri = iri;
		this.feature = feature;
		this.description = description;
		initProperties();
	}
	
	public GTFeature(IIRI iri, SimpleFeature feature){
		this(iri, feature, null);
	}
	
	public GTFeature(SimpleFeature feature){
		this(new IRI(feature.getID()), feature, null);
	}
	
	public SimpleFeature getFeature(){
		return feature;
	}
	
	public String getID(){
		return feature.getID();
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public boolean isBlank() {
		return iri == null;
	}

	@Override
	public IFeatureDescription getDescription() {
		return description;
	}
	
	public void setDescription(IFeatureDescription description) {
		this.description = description;
	}

	@Override
	public Collection<ISpatialProperty> getSpatialProperties() {
		return spatialProperties;
	}

	@Override
	public Collection<IThematicProperty> getThematicProperties() {
		return thematicProperties;
	}
	
	@Override
	public Collection<ITemporalProperty> getTemporalProperties() {
		//TODO: get description
		throw new UnsupportedOperationException("temporal properties cannot be resolved");
	}
	

	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
	}
	
	@Override
	public ISpatialProperty getDefaultSpatialProperty() {
		return new GTSpatialProperty((GeometryAttribute) getFeature().getDefaultGeometryProperty());
	}
	
	private void initProperties(){
		spatialProperties = new ArrayList<ISpatialProperty>();
		thematicProperties = new ArrayList<IThematicProperty>();
		for(Property property : this.feature.getProperties()){
			if(property instanceof GeometryAttribute)
				spatialProperties.add(new GTSpatialProperty((GeometryAttribute) property));
			else
				thematicProperties.add(new GTThematicProperty(property));
		}
	}

	@Override
	public IFeatureProperty getFeatureProperty(String identifier) {
		for(IThematicProperty property : getThematicProperties()){
			if(property.getIdentifier().equals(identifier))
				return property;
		}
		for(ISpatialProperty property : getSpatialProperties()){
			if(property.getIdentifier().equals(identifier))
				return property;
		}
		return null;
	}
	
//	private static class GTFeatureIdentifier {
//		
//		private final String ATT_BEGIN = "[";
//		private final String ATT_END = "]";
//		private final String ATT_INDICATOR = "att:";
//		private final String ATT_FID = "featureID";
//		private String defaultPattern = ATT_BEGIN + ATT_INDICATOR + ATT_FID + ATT_END;
//		
//		private String pattern;
//		
//		public GTFeatureIdentifier(String pattern){
//			this.pattern = (pattern == null || pattern.length() == 0 ? defaultPattern : pattern);
//		}
//		
//		public URI getIdentifier(GTFeature gtFeature) {
//			ArrayList<String> patternArray = parseURIPattern(pattern);
//			String sURI = getURIString(gtFeature, patternArray);
//			return URI.create(sURI);
//		}
//		
//		private ArrayList<String> parseURIPattern(String pattern){
//			ArrayList<String> patternArray = new ArrayList<String>();
//			String[] components = pattern.split("[" + Pattern.quote(ATT_BEGIN + "|" + ATT_END) + "]");
//			for(String component : components){
//				if(component.length() > 0)
//					patternArray.add(component);
//			}
//			return patternArray;
//		}
//		
//		private String getURIString(GTFeature gtFeature, ArrayList<String> patternArray){
//			if(patternArray == null || patternArray.size() == 0) 
//				return getFIDs(gtFeature);
//			StringBuffer sURI = new StringBuffer();
//			for(String pattern : patternArray){
//				if(pattern.startsWith(ATT_INDICATOR)){
//					String attName = pattern.substring(ATT_INDICATOR.length());
//					if(attName.equalsIgnoreCase(ATT_FID))
//						sURI.append(getFIDs(gtFeature));
//					else
//						sURI.append(getAtts(gtFeature, attName));
//				}
//				else
//					sURI.append(pattern);
//			}
//			return sURI.toString();
//		}
//		
//		private String getFIDs(GTFeature gtFeature){
//			StringBuffer sFID = new StringBuffer();
//			for(SimpleFeature feature : gtFeature.getFeatures()){
//				sFID.append("," + feature.getID());
//			}
//			return sFID.toString().substring(1);
//		}
//		
//		private String getAtts(GTFeature gtFeature, String name){
//			StringBuffer sFID = new StringBuffer();
//			for(SimpleFeature feature : gtFeature.getFeatures()){
//				sFID.append("," + feature.getAttribute(name));
//			}
//			return sFID.toString().substring(1);
//		}
//		
//	}

}
