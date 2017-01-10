package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Set;

/**
 * feature relation implementation
 */
public class BinaryFeatureRelation<T extends IFeature> extends BinaryRelation<T> {

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param domain relation domain
	 * @param range relation range
	 * @param type relation types
	 * @param metadata relation metadata
	 * @param measurements relation measurements
	 */
	public BinaryFeatureRelation(IIdentifier identifier, T domain, T range, IBinaryRelationType type, IMetadataForData metadata, Set<IRelationMeasurement> measurements) {
        super(identifier, domain, range, type, metadata, measurements);
    }
	
}
