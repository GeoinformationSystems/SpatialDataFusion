package de.tudresden.gis.fusion.data.relation;

import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;

/**
 * relation type implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class RelationType extends Resource implements IRelationType {
	
	private IResource referenceRole, targetRole;
	private boolean symmetric, transitive, reflexive;
	private IRelationType inverse;

	/**
	 * constructor
	 * @param identifier type identifier
	 * @param referenceRole reference role
	 * @param targetRole target role
	 * @param symmetric
	 * @param transitive
	 * @param reflexive
	 */
	public RelationType(String identifier, IResource referenceRole, IResource targetRole, boolean symmetric, boolean transitive, boolean reflexive) {
		super(identifier);
		this.referenceRole = referenceRole;
		this.targetRole = targetRole;
		this.symmetric = symmetric;
		this.transitive = transitive;
		this.reflexive = reflexive;
	}

	@Override
	public IResource getSource() {
		return referenceRole;
	}

	@Override
	public IResource getTarget() {
		return targetRole;
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

	@Override
	public IRelationType getInverse() {
		return inverse;
	}
	
	/**
	 * set inverse relation type
	 * @param inverse inverse relation type
	 */
	public void addInverse(IRelationType inverse){
		this.inverse = inverse;
	}

}
