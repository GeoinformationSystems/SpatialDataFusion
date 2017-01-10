package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.MeasurementData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.metadata.IMeasurementRange;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForMeasurement;
import de.tudresden.geoinfo.fusion.metadata.MeasurementRange;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Boolean literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class LongLiteral extends LiteralData<Long> {

	private static IResource TYPE = Objects.LONG.getResource();

	/**
	 * constructor
	 * @param value literal object
	 * @param metadata literal description
	 */
	public LongLiteral(long value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public LongLiteral(long value){
		this(value, null);
	}

	@Override
	public IResource getType() {
		return TYPE;
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static IMeasurementRange<Long> getMaxRange(){
		return getRange(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	/**
	 * get positive range for this literal type
	 * @return maximum range
	 */
	public static IMeasurementRange<Long> getPositiveRange(){
		return getRange(0, Long.MAX_VALUE);
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static IMeasurementRange<Long> getRange(long min, long max){
		SortedSet<Long> range = new TreeSet<>();
		range.add(min);
		range.add(max);
		return new MeasurementRange<>(range, true);
	}

	/**
	 * get measurement instance from integer value
	 * @param value integer value
	 * @return measurement instance
	 */
	public static MeasurementData<Long> getMeasurement(long value, IMetadataForMeasurement metadata){
		return new MeasurementData<>(value, TYPE, metadata);
	}
	
}
