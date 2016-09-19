package de.tud.fusion.data.literal;

import java.net.URI;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;

/**
 * URI literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class URILiteral extends Literal<URI> {

	/**
	 * constructor
	 * @param identifier literal identifier
	 * @param object literal object
	 * @param description literal description
	 */
	public URILiteral(String identifier, URI value, IDataDescription description) {
		super(identifier, value, description);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public URILiteral(URI value){
		this(null, value, null);
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.ANYURI.getResource();
	}
	
	/**
	 * get RegEx for URI validation; !note: the RegEx is rather permissive!
	 * @return URI regex string
	 */
	public static String getURIRegex() {
		return "" +
			"(https?|file|ftp):" +	//scheme
			"//[^\\?#]*" +			//authority
			"[^\\?#]*" +			//path
			"(\\?[^#]*)?" +			//query
			"(#\\w*)?";				//fragment
	}
	
}
