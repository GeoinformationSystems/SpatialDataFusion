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
 * Decimal literal implementation
 */
public class DecimalLiteral extends LiteralData<Double> {

	private static IResource TYPE = Objects.DECIMAL.getResource();

	/**
	 * constructor
	 * @param value literal object
	 * @param metadata literal description
	 */
	public DecimalLiteral(double value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value literal value
	 */
	public DecimalLiteral(double value){
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
	public static IMeasurementRange<Double> getMaxRange() {
        return getRange(Double.MIN_VALUE, Double.MAX_VALUE);
    }
	
	/**
	 * get positive range for this literal type
	 * @return maximum range
	 */
	public static IMeasurementRange<Double> getPositiveRange(){
		return getRange(0d, Double.MAX_VALUE);
	}

	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
    public static IMeasurementRange<Double> getRange(double min, double max){
		SortedSet<Double> range = new TreeSet<>();
		range.add(min);
		range.add(max);
		return new MeasurementRange<>(range, true);
	}
	
	/**
	 * get measurement instance from double value
	 * @param value double value
	 * @return measurement instance
	 */
	public static MeasurementData<Double> getMeasurement(double value, IMetadataForMeasurement metadata){
        return new MeasurementData<>(value, TYPE, metadata);
	}
	
}
