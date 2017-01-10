package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.Set;

/**
 * Operation description instance
 */
public class MetadataForOperation extends Metadata implements IMetadataForOperation {
	
	private Set<IIdentifier> inputIdentifier;
	private Set<IIdentifier> outputIdentifier;

	/**
	 * constructor
	 * @param title data title
	 * @param description data description
	 * @param inputIdentifier operation input identifier
	 * @param outputIdentifier operation output identifier
	 */
	public MetadataForOperation(String title, String description, Set<IIdentifier> inputIdentifier, Set<IIdentifier> outputIdentifier) {
		super(title, description);
		this.inputIdentifier = inputIdentifier;
		this.outputIdentifier = outputIdentifier;
	}

    @Override
    public Set<IIdentifier> getInputIdentifier() {
        return inputIdentifier;
    }

    @Override
    public Set<IIdentifier> getOutputIdentifier() {
        return outputIdentifier;
    }

}
