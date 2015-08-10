package de.tudresden.gis.fusion.data.binding.literal;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IRI;

public class XMLStringBinding implements ILiteral<XMLStringBinding> {

	private static final IRI IRI = new IRI("http://www.w3.org/2001/XMLSchema/#string");
	private String value;
	
	public XMLStringBinding(String value){
		this.value = value;
	}
	
	@Override
	public IRI getIdentifier() {
		return IRI;
	}

	@Override
	public Class<?> getJavaBinding() {
		return String.class;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public int compareTo(XMLStringBinding o) {
		return this.getValue().compareTo(o.getValue());
	}
	
}
