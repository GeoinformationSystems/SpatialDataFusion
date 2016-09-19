package de.tud.fusion.data.literal;

import java.time.LocalDateTime;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * Boolean literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class TimestampLiteral extends Literal<LocalDateTime> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public TimestampLiteral(String identifier, LocalDateTime value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public TimestampLiteral(LocalDateTime value){
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.TIME_INSTANT.getResource();
	}
	
}
