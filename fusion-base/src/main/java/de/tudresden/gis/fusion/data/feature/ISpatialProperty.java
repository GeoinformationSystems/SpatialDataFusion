package de.tudresden.gis.fusion.data.feature;

import de.tudresden.gis.fusion.data.rdf.IIRI;

public interface ISpatialProperty extends IFeatureProperty {
	
	/**
	 * get bounds for geometry (minx, miny, maxx, maxy)
	 * @return geometry bounds
	 */
	public double[] getBounds();
	
	/**
	 * get spatial property as WKT
	 * @return WKT representation of spatial property
	 */
	public String asWKT();
	
	/**
	 * get geometry type
	 * @return geometry type
	 */
	public EGeometryType getGeometryType();
	
	/**
	 * get name of the geometry CRS
	 * @return CRS
	 */
	public IIRI getSRSName();

}
