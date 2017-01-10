package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.Set;

public interface IMetadataForOperation extends IMetadata {

    /**
     * get identifier for input connectors
     * @return connector identifiers
     */
    Set<IIdentifier> getInputIdentifier();

    /**
     * get identifier for input connectors
     * @return connector identifiers
     */
    Set<IIdentifier> getOutputIdentifier();
	
}
