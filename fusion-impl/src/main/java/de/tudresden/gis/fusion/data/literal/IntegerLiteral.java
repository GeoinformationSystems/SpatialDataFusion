package de.tudresden.gis.fusion.data.literal;

import java.util.Arrays;
import java.util.TreeSet;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.MeasurementRange;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class IntegerLiteral extends AbstractMeasurement<Integer> implements ITypedLiteral {

	/**
	 * constructor
	 * @param value integer literal value
	 * @param description literal description 
	 */
	public IntegerLiteral(int value, IMeasurementDescription description){
		super(value, description);
	}
	
	/**
	 * constructor
	 * @param value integer literal value
	 */
	public IntegerLiteral(int value){
		this(value,	null);
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		if(measurement instanceof IntegerLiteral)
			return this.resolve().compareTo(((IntegerLiteral) measurement).resolve());
		else
			throw new ClassCastException("Cannot cast to IntegerLiteral");
	}

	@Override
	public String getValue() {
		return String.valueOf(resolve());
	}
	
	@Override
	public IResource getType() {
		return RDFVocabulary.INTEGER.getResource();
	}
	
	/**
	 * get maximum range for this literal type
	 * @return maximum range
	 */
	public static MeasurementRange maxRange(){
		return new MeasurementRange(new TreeSet<IntegerLiteral>(Arrays.asList(new IntegerLiteral(Integer.MIN_VALUE), new IntegerLiteral(Integer.MAX_VALUE))), true);
	}
	
	/**
	 * get positive range for this literal type
	 * @return positive range
	 */
	public static MeasurementRange positiveRange(){
		return new MeasurementRange(new TreeSet<IntegerLiteral>(Arrays.asList(new IntegerLiteral(0), new IntegerLiteral(Integer.MAX_VALUE))), true);
	}
}
