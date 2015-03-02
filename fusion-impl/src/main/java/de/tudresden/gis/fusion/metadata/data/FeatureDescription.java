package de.tudresden.gis.fusion.metadata.data;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.metadata.data.IFeatureDescription;

public class FeatureDescription extends DataDescription implements IFeatureDescription {
	
	public FeatureDescription(IIRI iri, String description) {
		super(iri, description);
	}

}
