package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class StringLiteral extends AbstractMeasurement<String> implements ITypedLiteral {

	/**
	 * constructor
	 * @param value string literal value
	 * @param description literal description 
	 */
	public StringLiteral(String value, IMeasurementDescription description){
		super(value, description);
	}
	
	/**
	 * constructor
	 * @param value string literal value
	 */
	public StringLiteral(String value){
		this(value, null);
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		return this.resolve().compareTo(measurement.resolve().toString());
	}

	@Override
	public String getValue() {
		return resolve();
	}
	
	@Override
	public IResource getType() {
		return RDFVocabulary.STRING.getResource();
	}
}
