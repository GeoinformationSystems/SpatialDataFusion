package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForMeasurement;

public class MeasurementData<T extends Comparable<T>> extends LiteralData<T> implements IMeasurementData<T> {

	/**
	 * constructor
	 * @param value measurement value
	 * @param type value data type
	 * @param metadata measurement metadata
	 */
	public MeasurementData(T value, IResource type, IMetadataForMeasurement metadata) {
		super(value, type, metadata);
	}

	@Override
	public IMetadataForMeasurement getMetadata() {
		return (IMetadataForMeasurement) super.getMetadata();
	}

	@Override
	public int compareTo(T o) {
		return this.resolve().compareTo(o);
	}
}
