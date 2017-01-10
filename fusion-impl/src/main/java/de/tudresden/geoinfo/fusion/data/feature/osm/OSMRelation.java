package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelationType;
import de.tudresden.geoinfo.fusion.data.relation.IRole;
import de.tudresden.geoinfo.fusion.data.relation.Relation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Map;
import java.util.Set;

/**
 * OSM relation
 */
public class OSMRelation extends Relation<OSMVectorFeature> {

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param members relation members with role
	 * @param type relation types
	 * @param metadata relation metadata
	 */
	public OSMRelation(IIdentifier identifier, Map<IRole,Set<OSMVectorFeature>> members, IRelationType type, IMetadataForData metadata){
		super(identifier, members, type, metadata);
	}
	
}