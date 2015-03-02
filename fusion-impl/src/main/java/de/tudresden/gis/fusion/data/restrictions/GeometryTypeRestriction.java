package de.tudresden.gis.fusion.data.restrictions;

import java.util.Collection;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.manage.Namespace;

public class GeometryTypeRestriction extends IORestriction {
	
	private final String RESTRICTION_URI = Namespace.uri_restriction() + "/property/spatial/geometry#type";
	
	EGeometryType[] types;
	
	public GeometryTypeRestriction(EGeometryType... types){
		this.types = types;
	}

	@Override
	public boolean compliantWith(IData input) {
		if(input instanceof IFeature){
			Collection<ISpatialProperty> properties = ((IFeature) input).getSpatialProperties();
			for(ISpatialProperty property : properties){
				if(!geometryTypeIsSupported(property)){
					return false;
				}
			}
		}			
		return true;
	}
	
	private boolean geometryTypeIsSupported(ISpatialProperty property){
		for(EGeometryType type : types){
			if(type.equals(property.getGeometryType()))
				return true;
		}
		return false;
	}

	@Override
	public String getAbstract() {
		return "feature geometry type restriction";
	}

	@Override
	public String getRestrictionURI() {
		return RESTRICTION_URI;
	}
	
}
