package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

public interface IComplexData extends IData,IRDFTripleSet {
	
	public IIRI getIdentifier();
	
}
