package de.tudresden.gis.fusion.data.custom;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.AFeatureInstance;

public class IRIFeatureInstance extends AFeatureInstance {

	public IRIFeatureInstance(IRI identifier) {
		super(identifier);
	}
	
	@Override
	public IRI value(){
		return this.identifier();
	}

}
