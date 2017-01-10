package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * MeasurementData description implementation
 */
public class MetadataForMeasurement extends MetadataForData implements IMetadataForMeasurement {
	
	private IResource measurementOperation;
	private IMeasurementRange range;
	private IResource uom;

	/**
	 * constructor
	 * @param title measurement title
	 * @param description measurement description
	 * @param dataType measurement data type
	 * @param measurementOperation measurement operation
	 * @param range measurement range
	 * @param uom unit of measurements
	 */
	public MetadataForMeasurement(String title, String description, IResource dataType, IResource measurementOperation, IMeasurementRange range, IResource uom) {
		super(title, description, dataType);
		this.measurementOperation = measurementOperation;
		this.range = range;
		this.uom = uom;
	}
	
	@Override
	public IResource getMeasurementOperation() {
		return measurementOperation;
	}

	@Override
	public IResource getUnitOfMeasurement() {
		return uom;
	}

}
