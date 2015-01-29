package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;

public class BooleanLiteral implements ISimpleData,ITypedLiteral,IMeasurementValue<Boolean> {

	private boolean value;
	
	public BooleanLiteral(boolean value){
		this.value = value;
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_BOOLEAN.resource();
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
	public int compareTo(IMeasurementValue<Boolean> target) {
		return ((Boolean) value).compareTo(target.getValue());
	}

	@Override
	public Boolean getValue() {
		return value;
	}

}
