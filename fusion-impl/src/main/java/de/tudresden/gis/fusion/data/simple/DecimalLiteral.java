package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class DecimalLiteral implements ISimpleData,ITypedLiteral,IMeasurementValue<Double> {

	private double value;
	
	public DecimalLiteral(double value){
		this.value = value;
	}

	@Override
	public IIdentifiableResource getType() {
		return new IdentifiableResource(ERDFNamespaces.LITERAL_TYPE_DECIMAL.asString());
	}

	@Override
	public String getIdentifier() {
		return String.valueOf(value);
	}

	@Override
	public IDataDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
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
