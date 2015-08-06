package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.manage.DataUtilities;

public class MeasurementValue extends Literal implements IMeasurementValue<Object> {

	private IMeasurementValue<?> value;
	
	public MeasurementValue(Object value) {
		this(value.toString());
	}
	
	public MeasurementValue(String value) {
		super(value);
		this.value = (IMeasurementValue<?>) DataUtilities.encodeLiteral(value);
	}

	@Override
	public int compareTo(IMeasurementValue<Object> input) {
		return this.compareToAny(input);
	}
	
	public int compareToAny(IMeasurementValue<?> input) {
		//get values
		Object v1 = this.value.getValue();
		Object v2 = input.getValue();
		//compare
		if(v1 instanceof Number && v2 instanceof Number)
			return ((Double) ((Number) v1).doubleValue()).compareTo(((Number) v2).doubleValue());
		else if(v1 instanceof Boolean && v2 instanceof Boolean)
			return ((Boolean) v1).compareTo((Boolean) v2);
		else
			return 1;
	}

	@Override
	public IIdentifiableResource getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
