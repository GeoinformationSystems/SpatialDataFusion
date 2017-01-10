package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Arrays;

/**
 * Relation type implementation
 */
public class BinaryRelationType extends RelationType implements IBinaryRelationType {

    private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
    private static IResource RELATION_TYPE = Objects.RELATION_TYPE.getResource();
    private static IResource ROLE_OF_DOMAIN = Predicates.ROLE_OF_DOMAIN.getResource();
    private static IResource ROLE_OF_RANGE = Predicates.ROLE_OF_RANGE.getResource();

	private IRole roleOfDomain, roleOfRange;
	private boolean symmetric, transitive, reflexive;

	/**
	 * constructor
	 * @param identifier type identifier
	 * @param description relation type description
	 * @param roleOfDomain reference role
	 * @param roleOfRange target role
	 * @param symmetric flag: relation is symmetric
	 * @param transitive flag: relation is transitive
	 * @param reflexive flag: relation is reflexive
	 */
	public BinaryRelationType(IIdentifier identifier, IMetadataForData description, IRole roleOfDomain, IRole roleOfRange, boolean symmetric, boolean transitive, boolean reflexive) {
		super(identifier, Arrays.asList(roleOfDomain, roleOfRange), description);
		this.roleOfDomain = roleOfDomain;
		this.roleOfRange = roleOfRange;
		this.symmetric = symmetric;
		this.transitive = transitive;
		this.reflexive = reflexive;
		//set resource type
		put(PREDICATE_TYPE, RELATION_TYPE);
		//set objects
		put(ROLE_OF_DOMAIN, roleOfDomain);
		put(ROLE_OF_RANGE, roleOfRange);
	}

	@Override
	public IRole getRoleOfDomain() {
		return roleOfDomain;
	}

	@Override
	public IRole getRoleOfRange() {
		return roleOfRange;
	}

	@Override
	public boolean isSymmetric() {
		return symmetric;
	}

	@Override
	public boolean isTransitive() {
		return transitive;
	}

	@Override
	public boolean isReflexive() {
		return reflexive;
	}

}
