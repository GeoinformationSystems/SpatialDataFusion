package de.tudresden.gis.fusion.data.restrictions;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.manage.Namespace;

public class MandatoryIORestriction extends IORestriction {

	private final String RESTRICTION_URI = Namespace.uri_restriction() + "/property#mandatory";
	
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
	public String getAbstract() {
		return "mandatory IO restriction";
	}

	@Override
	public String getRestrictionURI() {
		return RESTRICTION_URI;
	}
	
}
