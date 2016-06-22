package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class WKTLiteral extends StringLiteral {

	public WKTLiteral(String value, IMeasurementDescription description){
		super(value, description);
	}
	
	public WKTLiteral(String value){
		this(value, null);
	}
	
	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.WKT_LITERAL.asResource();
	}
	
}
