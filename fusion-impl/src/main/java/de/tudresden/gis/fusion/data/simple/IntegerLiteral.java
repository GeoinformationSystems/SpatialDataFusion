package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class IntegerLiteral extends Literal implements IMeasurementValue<Integer> {

	private int value;
	
	public IntegerLiteral(int value){
		super(String.valueOf(value));
		this.value = value;
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_INTEGER.resource();
	}
	
	@Override
	public int compareTo(IMeasurementValue<Integer> target) {
		return ((Integer) value).compareTo(target.getValue());
	}

	@Override
	public Integer getValue() {
		return value;
	}

}
