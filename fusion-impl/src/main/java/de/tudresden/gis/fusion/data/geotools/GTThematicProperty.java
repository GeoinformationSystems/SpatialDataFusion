package de.tudresden.gis.fusion.data.geotools;

import org.opengis.feature.Property;

import de.tudresden.gis.fusion.data.feature.IThematicProperty;

public class GTThematicProperty implements IThematicProperty {

	private Property property;
	
	public GTThematicProperty(Property property){
		this.property = property;
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

}
