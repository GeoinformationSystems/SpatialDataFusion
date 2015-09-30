package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class MeasurementDescription extends DataDescription implements IMeasurementDescription {
	
	private IMeasurementRange range;
	private IIdentifiableResource uom;

	public MeasurementDescription(String identifier, String title, String description, IMeasurementRange range, IIdentifiableResource uom) {
		super(identifier, title, description);
		this.range = range;
		this.uom = uom;
	}
	
	public MeasurementDescription(String title, String description, IMeasurementRange range, IIdentifiableResource uom) {
		this(null, title, description, range, uom);
	}
	
	@Override
	public IMeasurementRange getRange() {
		return range;
	}

	@Override
	public IIdentifiableResource getUnitOfMeasurement() {
		return uom;
	}

}
