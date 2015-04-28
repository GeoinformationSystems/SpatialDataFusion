package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;

public class BooleanLiteral extends Literal implements IMeasurementValue<Boolean> {

	private boolean value;
	
	public BooleanLiteral(boolean value){
		super(String.valueOf(value));
		this.value = value;
	}
	
	public BooleanLiteral(String value){
		super(value);
		this.value = Boolean.parseBoolean(value);
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_BOOLEAN.resource();
	}

	@Override
	public int compareTo(IMeasurementValue<Boolean> target) {
		return ((Boolean) value).compareTo(target.getValue());
	}

	@Override
	public Boolean getValue() {
		return value;
	}

}
