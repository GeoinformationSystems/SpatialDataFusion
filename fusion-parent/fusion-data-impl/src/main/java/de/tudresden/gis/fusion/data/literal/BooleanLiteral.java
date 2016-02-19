package de.tudresden.gis.fusion.data.literal;

import java.util.Arrays;
import java.util.TreeSet;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.MeasurementRange;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class BooleanLiteral extends AbstractMeasurement<Boolean> implements ITypedLiteral {
	
	public BooleanLiteral(boolean value, IMeasurementDescription description){
		super(value, description);
	}
	
	public BooleanLiteral(boolean value){
		this(value,	null);
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		if(measurement instanceof BooleanLiteral)
			return this.resolve().compareTo(((BooleanLiteral) measurement).resolve());
		else
			throw new ClassCastException("Cannot cast to BooleanLiteral");
	}

	@Override
	public String getValue() {
		return String.valueOf(resolve());
	}
	
	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.BOOLEAN.asResource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static MeasurementRange maxRange(){
		return new MeasurementRange(new TreeSet<BooleanLiteral>(Arrays.asList(new BooleanLiteral(true), new BooleanLiteral(false))), false);
	}

}
