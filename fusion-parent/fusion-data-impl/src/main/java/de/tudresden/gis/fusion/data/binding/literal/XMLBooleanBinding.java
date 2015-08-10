package de.tudresden.gis.fusion.data.binding.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IRI;

public class XMLBooleanBinding implements ILiteral<XMLBooleanBinding> {

	private static final IRI IRI = new IRI("http://www.w3.org/2001/XMLSchema/#boolean");
	private boolean value;
	
	public XMLBooleanBinding(boolean value){
		this.value = value;
	}
	
	@Override
	public IRI getIdentifier() {
		return IRI;
	}

	@Override
	public Class<?> getJavaBinding() {
		return Boolean.class;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public int compareTo(XMLBooleanBinding o) {
		return this.getValue().compareTo(o.getValue());
	}

}
