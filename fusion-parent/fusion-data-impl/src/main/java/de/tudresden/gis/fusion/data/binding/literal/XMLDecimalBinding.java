package de.tudresden.gis.fusion.data.binding.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IRI;

public class XMLDecimalBinding implements ILiteral<XMLDecimalBinding> {

	private static final IRI IRI = new IRI("http://www.w3.org/2001/XMLSchema/#decimal");
	private double value;
	
	public XMLDecimalBinding(double value){
		this.value = value;
	}
	
	@Override
	public IRI getIdentifier() {
		return IRI;
	}

	@Override
	public Class<?> getJavaBinding() {
		return Double.class;
	}
	
	@Override
	public Double getValue() {
		return value;
	}
	
	@Override
	public int compareTo(XMLDecimalBinding o) {
		return this.getValue().compareTo(o.getValue());
	}

}
