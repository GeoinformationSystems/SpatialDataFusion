package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;

public class DecimalLiteral extends Literal implements IMeasurementValue<Double> {

	private double value;
	
	public DecimalLiteral(double value){
		super(String.valueOf(value));
		this.value = value;
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_DECIMAL.resource();
	}
	
	@Override
	public int compareTo(IMeasurementValue<Double> target) {
		return ((Double) value).compareTo(target.getValue());
	}

	@Override
	public Double getValue() {
		return value;
	}

}
