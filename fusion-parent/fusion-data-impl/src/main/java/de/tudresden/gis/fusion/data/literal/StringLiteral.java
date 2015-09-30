package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteralData;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class StringLiteral implements ILiteralData,IMeasurement,ITypedLiteral {

	private String value;
	private IMeasurementDescription description;
	
	public StringLiteral(String value, IMeasurementDescription description){
		this.value = value;
		this.description = description;
	}
	
	public StringLiteral(String value){
		this(value, null);
	}

	@Override
	public String resolve() {
		return value;
	}

	@Override
	public IMeasurementDescription getDescription() {
		return description;
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		return this.resolve().compareTo(measurement.resolve().toString());
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.STRING.asResource();
	}
}
