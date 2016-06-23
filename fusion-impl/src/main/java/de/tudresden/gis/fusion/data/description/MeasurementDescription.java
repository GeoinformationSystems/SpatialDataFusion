package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * measurement description implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class MeasurementDescription extends DataDescription implements IMeasurementDescription {
	
	/**
	 * range of the measurement
	 */
	private IMeasurementRange range;
	
	/**
	 * unit of measurement
	 */
	private IResource uom;

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param title measurement title
	 * @param description measurement description
	 * @param range measurement range
	 * @param uom measurement uom
	 */
	public MeasurementDescription(String identifier, String title, String description, IMeasurementRange range, IResource uom) {
		super(identifier, title, description);
		this.range = range;
		this.uom = uom;
	}
	
	/**
	 * constructor
	 * @param title measurement title
	 * @param description measurement description
	 * @param range measurement range
	 * @param uom measurement uom
	 */
	public MeasurementDescription(String title, String description, IMeasurementRange range, IResource uom) {
		this(null, title, description, range, uom);
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
