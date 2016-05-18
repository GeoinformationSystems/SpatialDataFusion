package de.tudresden.gis.fusion.operation.constraint;

public class ContraintFactory {
	
	public static MandatoryConstraint getMandatoryConstraint(String identifier){
		return new MandatoryConstraint(identifier);
	}
	
	public static BindingConstraint getBindingConstraint(Class<?>[] bindings){
		return new BindingConstraint(bindings);
	}

}
