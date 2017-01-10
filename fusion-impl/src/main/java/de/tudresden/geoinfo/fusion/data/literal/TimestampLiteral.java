package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.time.LocalDateTime;

/**
 * Boolean literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class TimestampLiteral extends LiteralData<LocalDateTime> {

	private static IResource TYPE = Objects.TIME_INSTANT.getResource();

	/**
	 * constructor
	 * @param value literal object
	 * @param metadata literal description
	 */
	public TimestampLiteral(LocalDateTime value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public TimestampLiteral(LocalDateTime value){
		this(value, null);
	}

	@Override
	public IResource getType() {
		return TYPE;
	}
	
}
