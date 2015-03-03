package de.tudresden.gis.fusion.data.geotools;

import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.referencing.CRS;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.Resource;

public class GTSpatialProperty implements ISpatialProperty {

	private GeometryAttribute property;
	private double[] bounds;
	
	public GTSpatialProperty(GeometryAttribute property){
		this.property = property;
		setBounds(property.getBounds());
	}
	
	public GTSpatialProperty(GeometryDescriptor descriptor, String identifier, BoundingBox bounds){
		this.property = new GeometryAttributeImpl(null, descriptor, new FeatureIdImpl(identifier));
		setBounds(bounds);
	}
	
	private void setBounds(BoundingBox bounds){
		this.bounds = new double[]{bounds.getMinX(),bounds.getMinY(),bounds.getMaxX(),bounds.getMaxY()};
	}
	
	@Override
	public String getIdentifier() {
		return property.getName().toString();
	}

	@Override
	public Class<?> getJavaBinding() {
		return property.getType().getBinding();
	}

	@Override
	public Object getValue() {
		return property.getValue();
	}
	
	public double[] getBounds(){
		return this.bounds;
	}

	@Override
	public EGeometryType getGeometryType() {
		if(this.getJavaBinding().isAssignableFrom(Point.class))
			return EGeometryType.GML3_0D_POINT;
		else if (this.getJavaBinding().isAssignableFrom(MultiPoint.class))
			return EGeometryType.GML3_0D_MULTIPOINT;
		else if(this.getJavaBinding().isAssignableFrom(LineString.class))
			return EGeometryType.GML3_1D_CURVE;
		else if (this.getJavaBinding().isAssignableFrom(MultiLineString.class))
			return EGeometryType.GML3_1D_MULTICURVE;
		else if (this.getJavaBinding().isAssignableFrom(Polygon.class))
			return EGeometryType.GML3_2D_SURFACE;
		else if(this.getJavaBinding().isAssignableFrom(MultiPolygon.class))
			return EGeometryType.GML3_2D_MULTISURFACE;
		else
			return null;
	}

	@Override
	public IResource getSRS() {
		try {
			Integer iEPSG;
			CoordinateReferenceSystem crs = property.getDescriptor().getCoordinateReferenceSystem();
			if(crs == null)
				return Resource.newEmptyResource();
			iEPSG = CRS.lookupEpsgCode(crs, false);
			if(iEPSG == null)
				iEPSG = CRS.lookupEpsgCode(crs, true);
			if(iEPSG == null)
				return null;
			return new IdentifiableResource("http://www.opengis.net/def/crs/EPSG/0/" + iEPSG);
		} catch (FactoryException e) {
			return null;
		}
	}

	@Override
	public String asWKT() {
		return ((Geometry) property.getValue()).toText();
	}

}
