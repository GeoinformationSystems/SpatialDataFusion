package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.ITypedLiteral;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForMeasurement;

/**
 * Basic measurement object
 */
public interface IMeasurementData<T extends Comparable> extends IData,ITypedLiteral,Comparable<T> {

    @Override
    T resolve();

	@Override
    IMetadataForMeasurement getMetadata();
	
}
