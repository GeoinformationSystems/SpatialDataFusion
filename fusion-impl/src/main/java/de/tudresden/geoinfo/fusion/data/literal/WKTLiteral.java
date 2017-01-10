package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

/**
 * WKT literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WKTLiteral extends LiteralData<String> {

	private static IResource TYPE = Objects.WKT_LITERAL.getResource();

	/**
	 * constructor
	 * @param value literal object
	 * @param metadata literal description
	 */
	public WKTLiteral(String value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public WKTLiteral(String value){
		this(value, null);
	}

	@Override
	public IResource getType() {
		return TYPE;
	}
	
}
