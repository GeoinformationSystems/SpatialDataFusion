package de.tudresden.gis.fusion.data.feature;

public interface IFeatureProperty {
	
	public Object getValue();
	
	public String getIdentifier();
	
	public Class<?> getJavaBinding();

}
