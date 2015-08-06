package de.tudresden.gis.fusion.data.type;

import de.tudresden.gis.fusion.data.IRI;

public class LiteralStringType implements ILiteralType {
	
	public static final String IDENTIFIER = "http://www.w3.org/2001/XMLSchema/#string";

	public IRI getIdentifier() {
		return new IRI(IDENTIFIER);
	}

}
