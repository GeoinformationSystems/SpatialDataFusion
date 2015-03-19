package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;

public class LongLiteral extends Literal implements IMeasurementValue<Long> {

	private long value;
	
	public LongLiteral(long value){
		super(String.valueOf(value));
		this.value = value;
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_LONG.resource();
	}

	@Override
	public int compareTo(IMeasurementValue<Long> target) {
		return ((Long) value).compareTo(target.getValue());
	}

	@Override
	public Long getValue() {
		return value;
	}

}
