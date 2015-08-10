package de.tudresden.gis.fusion.data.binding.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IRI;

public class XMLIntegerBinding implements ILiteral<XMLIntegerBinding> {

	private static final IRI IRI = new IRI("http://www.w3.org/2001/XMLSchema/#integer");
	private int value;
	
	public XMLIntegerBinding(int value){
		this.value = value;
	}
	
	@Override
	public IRI getIdentifier() {
		return IRI;
	}

	@Override
	public Class<?> getJavaBinding() {
		return Integer.class;
	}
	
	@Override
	public Integer getValue() {
		return value;
	}
	
	@Override
	public int compareTo(XMLIntegerBinding o) {
		return this.getValue().compareTo(o.getValue());
	}

}
