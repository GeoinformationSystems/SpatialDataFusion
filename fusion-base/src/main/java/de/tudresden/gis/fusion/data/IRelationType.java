package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.IIRI;

public interface IRelationType extends IComplexData {
	
	public IIRI getIdentifier();

	public boolean equals(IRelationType type);
	
	public boolean isSubtypeOf(IRelationType type);
	
	public boolean isSupertypeOf(IRelationType type);
	
}
