package de.tudresden.geoinfo.fusion.data.relation;

/**
 * Binary relation type
 */
public interface IBinaryRelationType extends IRelationType {

	/**
	 * get role of the domain as specified by the relation type
	 * @return domain role
	 */
	IRole getRoleOfDomain();
	
	/**
	 * get role of the range as specified by the relation type
	 * @return range role
	 */
    IRole getRoleOfRange();
	
	/**
	 * check if a relation type is symmetric
	 * @return true if relation type is symmetric
	 */
    boolean isSymmetric();
	
	/**
	 * check if a relation type is transitive
	 * @return true if relation type is transitive
	 */
    boolean isTransitive();
	
	/**
	 * check if a relation type is reflexive
	 * @return true if relation type is reflexive
	 */
    boolean isReflexive();
	
}
