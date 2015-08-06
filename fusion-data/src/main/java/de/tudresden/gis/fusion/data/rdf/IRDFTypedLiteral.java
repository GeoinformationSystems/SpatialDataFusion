package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.value.ILiteralValue;

public interface IRDFTypedLiteral extends IRDFLiteral {

	@Override
	public ILiteralValue getValue();
	
}
