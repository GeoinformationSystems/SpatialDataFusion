package de.tudresden.geoinfo.fusion.metadata;

public interface IMetadataForMeasurementOperation extends IMetadataForOperation {

    /**
     * get range for the measurement operation
     * @return measurement range
     */
    IMeasurementRange getMeasurementRange();
	
}
