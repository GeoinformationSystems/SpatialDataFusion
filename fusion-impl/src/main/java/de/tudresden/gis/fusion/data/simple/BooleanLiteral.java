package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class BooleanLiteral extends Literal implements IMeasurementValue<Boolean> {

	private boolean value;
	
	public BooleanLiteral(boolean value){
		super(String.valueOf(value));
		this.value = value;
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
