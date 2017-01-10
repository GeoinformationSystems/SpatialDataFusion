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
public class BooleanLiteral extends LiteralData<Boolean> {

	private static IResource TYPE = Objects.BOOLEAN.getResource();

	/**
	 * constructor
	 * @param value literal value
	 * @param metadata literal measurement description
	 */
	public BooleanLiteral(boolean value, IMetadataForData metadata) {
		super(value, TYPE, metadata);
	}
	
	/**
	 * constructor
	 * @param value boolean value
	 */
	public BooleanLiteral(boolean value) {
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
	public static IMeasurementRange getMaxRange(){
        SortedSet<Boolean> range = new TreeSet<>();
        range.add(true);
        range.add(false);
		return new MeasurementRange<>(range, false);
	}

	/**
	 * get measurement instance from boolean value
	 * @param value boolean value
	 * @return measurement instance
	 */
	public static MeasurementData<Boolean> getMeasurement(boolean value, IMetadataForMeasurement metadata){
		return new MeasurementData<>(value, TYPE, metadata);
	}

}
