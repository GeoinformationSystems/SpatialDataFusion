package de.tudresden.gis.fusion.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

public interface IFeatureCollection extends IComplexData,Iterable<IFeature>,IRDFTripleSet {

	public int size();
	
	public Collection<IFeature> getFeatures();
	
	public IFeature getFeatureById(IIRI featureIRI);
	
	public ISpatialProperty getSpatialProperty();
	
}
