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
 * Integer literal implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class IntegerLiteral extends LiteralData<Integer> {

	private static IResource TYPE = Objects.INTEGER.getResource();

	/**
	 * constructor
	 * @param value literal object
	 * @param metadata literal description
	 */
	public IntegerLiteral(int value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value boolean literal value
	 */
	public IntegerLiteral(int value){
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
	public static IMeasurementRange<Integer> getMaxRange(){
		return getRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	/**
	 * get positive range for this literal type
	 * @return maximum range
	 */
	public static IMeasurementRange<Integer> getPositiveRange(){
		return getRange(0, Integer.MAX_VALUE);
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
    public static IMeasurementRange<Integer> getRange(int min, int max){
		SortedSet<Integer> range = new TreeSet<>();
		range.add(min);
		range.add(max);
		return new MeasurementRange<>(range, true);
	}
	
	/**
	 * get measurement instance from integer value
	 * @param value integer value
	 * @return measurement instance
	 */
	public static MeasurementData<Integer> getMeasurement(int value, IMetadataForMeasurement metadata){
        return new MeasurementData<>(value, TYPE, metadata);
	}
	
}
