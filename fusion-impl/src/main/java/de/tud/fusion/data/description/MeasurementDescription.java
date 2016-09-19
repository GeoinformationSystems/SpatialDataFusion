package de.tud.fusion.data.description;

import de.tud.fusion.data.rdf.IResource;

/**
 * Measurement description implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class MeasurementDescription extends DataDescription implements IMeasurementDescription {
	
	private IResource measurementType;
	private IMeasurementRange range;
	private IResource uom;

	/**
	 * constructor
	 * @param identifier description id
	 * @param title measurement title
	 * @param description measurement description
	 * @param type measurement type
	 * @param range measurement range
	 * @param uom unit of measurements
	 * @param bindings measurement bindings
	 */
	public MeasurementDescription(String identifier, String title, String description, IResource dataType, IResource measurementType, IMeasurementRange range, IResource uom) {
		super(identifier, title, description, dataType);
		this.measurementType = measurementType;
		this.range = range;
		this.uom = uom;
	}
	
	@Override
	public IResource getMeasurementType() {
		return measurementType;
	}
	
	@Override
	public IMeasurementRange getRange() {
		return range;
	}

	@Override
	public IResource getUnitOfMeasurement() {
		return uom;
	}

}
