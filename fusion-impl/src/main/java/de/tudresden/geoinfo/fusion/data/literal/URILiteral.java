package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.net.URI;

/**
 * URI literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class URILiteral extends LiteralData<URI> {

	private static IResource TYPE = Objects.ANYURI.getResource();

	/**
	 * constructor
	 * @param value literal object
	 * @param metadata literal description
	 */
	public URILiteral(URI value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}

    /**
	 * constructor
	 * @param value boolean literal value
	 */
	public URILiteral(URI value){
		this(value, null);
	}

	@Override
	public IResource getType() {
		return TYPE;
	}

	/**
	 * get RegEx for URL validation; !note: the RegEx is rather permissive!
	 * @return URL regex string
	 */
	public static String getURLRegex() {
		return "" +
				"(https?|file|ftp):" +	//scheme
				"//[^\\?#]*" +			//authority
				"[^\\?#]*" +			//path
				"(\\?[^#]*)?" +			//query
				"(#\\w*)?";				//fragment
	}
	
}
