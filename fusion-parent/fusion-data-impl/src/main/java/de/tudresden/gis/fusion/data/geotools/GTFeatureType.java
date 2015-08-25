package de.tudresden.gis.fusion.data.geotools;

import org.opengis.feature.type.FeatureType;

import de.tudresden.gis.fusion.data.IRI;

public class GTFeatureType extends de.tudresden.gis.fusion.data.feature.AFeatureType {
	
	private FeatureType type;
	
	public GTFeatureType(IRI identifier, FeatureType type){
		super(identifier);
		this.type = type;
	}
	
	public GTFeatureType(FeatureType type){
		this(new IRI(type.getName().toString()), type);
	}
	
	@Override
	public FeatureType value(){
		return type;
	}

}
