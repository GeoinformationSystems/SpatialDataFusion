package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IPlainLiteral;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;

public class StringLiteral extends Literal implements IPlainLiteral,ITypedLiteral,IMeasurementValue<String> {

	private String value;
	private String language;
	
	public StringLiteral(String value, String language){
		super(value);
		this.value = value;
		this.language = language;
	}
	
	public StringLiteral(String value){
		this(value, null);
	}

	@Override
	public IIdentifiableResource getType() {
		return ERDFNamespaces.LITERAL_TYPE_STRING.resource();
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
