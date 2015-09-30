package de.tudresden.gis.fusion.data.feature.geotools;

import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AbstractFeature;
import de.tudresden.gis.fusion.data.feature.FeatureEntity;
import de.tudresden.gis.fusion.data.feature.FeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.FeatureType;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;

public class GTFeature extends AbstractFeature<Feature> {
	
	public GTFeature(String identifier, Feature feature, IDataDescription description){
		super(identifier, feature, description);
	}
	
	public GTFeature(String identifier, Feature feature){
		this(identifier, feature, null);
	}
	
	public GTFeature(Feature feature){
		this(feature.getIdentifier().getID(), feature, null);
	}
	
	@Override
	public Feature resolve() {
		return (Feature) super.resolve();
	}
	
	/**
	 * get default geometry of this feature
	 * @return default feature geometry
	 */
	public Geometry getDefaultGeometry(){
		return (Geometry) this.resolve().getDefaultGeometryProperty().getValue();
	}

	@Override
	public IFeatureConcept initConcept(Feature feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureType initType(Feature feature) {
		return new FeatureType(feature.getType());
	}

	@Override
	public IFeatureEntity initEntity(Feature feature) {
		return new FeatureEntity(feature.getIdentifier().getID());
	}

	@Override
	public IFeatureRepresentation initRepresentation(Feature feature) {
		return new FeatureRepresentation(feature);
	}

}
