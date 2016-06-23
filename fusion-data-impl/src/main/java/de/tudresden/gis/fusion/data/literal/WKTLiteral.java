package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class WKTLiteral extends StringLiteral {

	/**
	 * constructor
	 * @param value WKT literal value
	 * @param description literal description 
	 */
	public WKTLiteral(String value, IMeasurementDescription description){
		super(value, description);
	}
	
	/**
	 * constructor
	 * @param value WKT literal value
	 */
	public WKTLiteral(String value){
		this(value, null);
	}
	
	@Override
	public IResource getType() {
		return RDFVocabulary.WKT_LITERAL.getResource();
	}
	
}
