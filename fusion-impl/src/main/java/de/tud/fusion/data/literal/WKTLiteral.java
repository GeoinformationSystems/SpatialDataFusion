package de.tud.fusion.data.literal;

import de.tud.fusion.data.description.IMeasurementDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * WKT literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WKTLiteral extends StringLiteral {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public WKTLiteral(String identifier, String value, IMeasurementDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public WKTLiteral(String value){
		super(value);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.WKT_LITERAL.getResource();
	}
	
}
