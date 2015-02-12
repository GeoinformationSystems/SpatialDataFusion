package de.tudresden.gis.fusion.data.feature;

public enum EGeometryType {

	GML3_0D_POINT("http://www.opengis.net/ont/gml#Point"),
	GML3_0D_MULTIPOINT("http://www.opengis.net/ont/gml#MultiPoint"),
	
	GML3_1D_CURVE("http://www.opengis.net/ont/gml#Curve"),
	GML3_1D_MULTICURVE("http://www.opengis.net/ont/gml#MultiCurve"),
	GML3_1D_LINESTRING("http://www.opengis.net/ont/gml#LineString"),
	
	GML3_2D_SURFACE("http://www.opengis.net/ont/gml#Surface"),
	GML3_2D_MULTISURFACE("http://www.opengis.net/ont/gml#MultiSurface"),
	GML3_2D_POLYGON("http://www.opengis.net/ont/gml#Polygon"),
	
	GML3_MULTIGEOMETRY("http://www.opengis.net/ont/gml#MultiGeometry"),
	
	GML3_SURFACE("http://www.opengis.net/ont/gml#Surface");
	
	//TODO: complete geometry and coverage type list
	
	private String type;
	
	private EGeometryType(String type){
		this.type = type;
	}
	
	public String getType(){ 
		return type; 
	}
	
}
