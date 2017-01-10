package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

/**
 * String literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class StringLiteral extends LiteralData<String> {

	private static IResource TYPE = Objects.STRING.getResource();

	/**
	 * constructor
	 * @param value literal value
	 * @param metadata literal description
	 */
	public StringLiteral(String value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public StringLiteral(String value){
		this(value, null);
	}

	@Override
	public IResource getType() {
		return TYPE;
	}
	
}
