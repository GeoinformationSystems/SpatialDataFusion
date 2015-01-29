package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;

public class IntegerLiteral implements ISimpleData,ITypedLiteral,IMeasurementValue<Integer> {

	private int value;
	
	public IntegerLiteral(int value){
		this.value = value;
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_INTEGER.resource();
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
	public int compareTo(IMeasurementValue<Integer> target) {
		return ((Integer) value).compareTo(target.getValue());
	}

	@Override
	public Integer getValue() {
		return value;
	}

}
