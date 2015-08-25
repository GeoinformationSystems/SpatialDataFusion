package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class BooleanLiteral implements ILiteral,IMeasurementValue<Boolean>,IRDFTypedLiteral {

	private boolean value;
	private IMeasurementDescription description;
	
	public BooleanLiteral(boolean value, IMeasurementDescription description){
		this.value = value;
		this.description = description;
	}
	
	public BooleanLiteral(boolean value){
		this(value,	null);
	}

	@Override
	public Boolean value() {
		return value;
	}

	@Override
	public IMeasurementDescription description() {
		return description;
	}

	@Override
	public int compareTo(Boolean o) {
		return this.value().compareTo(o);
	}

	@Override
	public ILiteral literalValue() {
		return this;
	}
	
	@Override
	public IRDFIdentifiableResource type() {
		return RDFVocabulary.TYPE_BOOLEAN.resource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static Range<Boolean> maxRange(){
		return new Range<Boolean>(new Boolean[]{true, false}, false);
	}

}
