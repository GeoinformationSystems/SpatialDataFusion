package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.Set;

/**
 * Generic measurement description
 */
public class MetadataForMeasurementOperation extends MetadataForOperation implements IMetadataForMeasurementOperation {

	private IMeasurementRange measurementRange;

	/**
	 * constructor
	 * @param title data title
	 * @param description data description
	 * @param inputIdentifier operation input identifier
	 * @param outputIdentifier operation output identifier
	 */
	public MetadataForMeasurementOperation(String title, String description, Set<IIdentifier> inputIdentifier, Set<IIdentifier> outputIdentifier, IMeasurementRange measurementRange) {
		super(title, description, inputIdentifier, outputIdentifier);
		this.measurementRange = measurementRange;
	}

    @Override
    public IMeasurementRange getMeasurementRange() {
        return this.measurementRange;
    }

}
