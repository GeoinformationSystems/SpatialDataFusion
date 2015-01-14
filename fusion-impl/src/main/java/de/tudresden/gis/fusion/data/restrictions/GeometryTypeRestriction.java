package de.tudresden.gis.fusion.data.restrictions;

import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;

public class GeometryTypeRestriction implements IDataRestriction {
	
	private final String RESTRICTION_URI = "http://tu-dresden.de/uw/geo/gis/fusion/restriction#geometryType";
	
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
	public IResource getSubject() {
		return new IdentifiableResource(RESTRICTION_URI);
	}

	@Override
	public Map<IIdentifiableResource, INode> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getIdentifier() {
		return getSubject().getIdentifier();
	}
	
}
