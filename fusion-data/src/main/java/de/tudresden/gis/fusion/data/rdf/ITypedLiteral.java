package de.tudresden.gis.fusion.data.rdf;

public interface ITypedLiteral extends ILiteral {
	
	/**
	 * get literal type identifier
	 * @return literal type identifier
	 */
	public IIdentifiableResource getType();
	
}
