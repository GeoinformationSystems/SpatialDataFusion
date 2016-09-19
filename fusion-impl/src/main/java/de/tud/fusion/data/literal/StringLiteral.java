package de.tud.fusion.data.literal;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * String literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class StringLiteral extends Literal<String> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public StringLiteral(String identifier, String value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public StringLiteral(String value){
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.STRING.getResource();
	}
	
}
