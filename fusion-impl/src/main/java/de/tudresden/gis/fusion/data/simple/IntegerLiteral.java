package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;

public class IntegerLiteral extends Literal implements IMeasurementValue<Integer> {

	private int value;
	
	public IntegerLiteral(int value){
		super(String.valueOf(value));
		this.value = value;
	}
	
	public IntegerLiteral(String value){
		super(value);
		this.value = Integer.parseInt(value);
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
