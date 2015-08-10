package de.tudresden.gis.fusion.data.binding.literal;

import java.net.URI;

import de.tudresden.gis.fusion.data.ILiteral;
import de.tudresden.gis.fusion.data.IRI;

public class XMLURIBinding implements ILiteral<XMLURIBinding> {

	private static final IRI IRI = new IRI("http://www.w3.org/2001/XMLSchema/#anyURI");
	private URI value;
	
	public XMLURIBinding(URI value){
		this.value = value;
	}
	
	@Override
	public IRI getIdentifier() {
		return IRI;
	}

	@Override
	public Class<?> getJavaBinding() {
		return URI.class;
	}
	
	@Override
	public URI getValue() {
		return value;
	}
	
	@Override
	public int compareTo(XMLURIBinding o) {
		return this.getValue().compareTo(o.getValue());
	}

}
