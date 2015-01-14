package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IPlainLiteral;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class StringLiteral implements ISimpleData,IPlainLiteral,ITypedLiteral,IMeasurementValue<String> {

	private String value;
	private String language;
	
	public StringLiteral(String value, String language){
		this.value = value;
		this.language = language;
	}
	
	public StringLiteral(String value){
		this(value, null);
	}

	@Override
	public IIdentifiableResource getType() {
		return new IdentifiableResource(ERDFNamespaces.LITERAL_TYPE_STRING.asString());
	}

	@Override
	public IDataDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getIdentifier() {
		return value;
	}

	@Override
	public String getLanguage() {
		if(language == null)
			return "";
		return language;
	}

	@Override
	public int compareTo(IMeasurementValue<String> target) {
		return value.compareTo(target.getValue());
	}

	@Override
	public String getValue() {
		return value;
	}
	
}
