package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.IRDFCollection;

public interface IFeatureRelationCollection extends IComplexData,Iterable<IFeatureRelation>,IRDFCollection {

	public int size();
	
	public void addRelation(IFeatureRelation relation);
	
}
