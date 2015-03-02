package de.tudresden.gis.fusion.data.restrictions;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.manage.Namespace;

public class JavaBindingRestriction extends IORestriction {
	
	private final String RESTRICTION_URI = Namespace.uri_restriction() + "/format/memory#binding";

	private Class<? extends IData>[] bindings;
	
	@SafeVarargs
	public JavaBindingRestriction(Class<? extends IData>... bindings){
		this.bindings = bindings;
	}

	@Override
	public boolean compliantWith(IData input) {
		for(Class<? extends IData> binding : bindings){
			if(binding.isAssignableFrom(input.getClass())){
				return true;
			}
		}
		return false;
	}
	
	public Class<? extends IData>[] getBindings(){
		return bindings;
	}
	
	public boolean compliantWith(Class<? extends IData> input) {
		for(Class<? extends IData> binding : bindings){
			if(binding.isAssignableFrom(input)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getAbstract() {
		return "JAVA object binding restriction";
	}

	@Override
	public String getRestrictionURI() {
		return RESTRICTION_URI;
	}
	
}
