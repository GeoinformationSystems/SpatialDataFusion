package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.ILiteral;

public interface IRDFLiteral extends IRDFNode {
	
	/**
	 * get value of this literal node
	 * @return node value
	 */
	public ILiteral getValue();
	
}
