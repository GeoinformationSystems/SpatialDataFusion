package de.tudresden.gis.fusion.data.geotools;

import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AFeatureRepresentation;

public class GTFeature extends AFeatureRepresentation {
	
	public GTFeature(IRI identifier, Feature feature, IDataDescription description){
		super(identifier, feature, description);
	}
	
	public GTFeature(IRI identifier){
		this(identifier, null, null);
	}
	
	public GTFeature(Feature feature){
		this(new IRI(feature.getIdentifier().getID()), feature, null);
	}
	
	@Override
	public Feature value() {
		return (Feature) super.value();
	}
	
	/**
	 * get default geometry of this feature
	 * @return default feature geometry
	 */
	public Geometry geometry(){
		return (Geometry) this.value().getDefaultGeometryProperty().getValue();
	}

}
