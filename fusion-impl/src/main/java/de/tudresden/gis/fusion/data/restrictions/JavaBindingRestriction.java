package de.tudresden.gis.fusion.data.restrictions;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;

public class JavaBindingRestriction implements IDataRestriction {
	
	private final String RESTRICTION_URI = "http://tu-dresden.de/uw/geo/gis/fusion/restriction#javaBinding";

	private Class<? extends IData>[] bindings;
	
	@SafeVarargs
	public JavaBindingRestriction(Class<? extends IData>... bindings){
		this.bindings = bindings;
	}

	@Override
	public boolean compliantWith(IData input) {
		boolean compliant = false;
		for(Class<? extends IData> binding : bindings){
			if(binding.isAssignableFrom(input.getClass())){
				compliant = true;
				break;
			}
		}
		return compliant;
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
