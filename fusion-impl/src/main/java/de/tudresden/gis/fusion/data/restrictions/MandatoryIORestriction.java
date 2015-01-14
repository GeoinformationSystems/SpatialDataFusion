package de.tudresden.gis.fusion.data.restrictions;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;

public class MandatoryIORestriction implements IDataRestriction {

	private final String RESTRICTION_URI = "http://tu-dresden.de/uw/geo/gis/fusion/restriction#mandatory";
	
	private boolean mandatory;
	
	public MandatoryIORestriction(boolean mandatory) {
		this.mandatory = mandatory;	
	}

	@Override
	public boolean compliantWith(IData input) {
		if(!this.mandatory)
			return true;
		else
			return (input != null);		
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
