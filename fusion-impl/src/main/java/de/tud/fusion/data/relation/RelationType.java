package de.tud.fusion.data.relation;

import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.Resource;

/**
 * Relation type implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class RelationType extends Resource implements IRelationType {
	
	private IResource source, target;
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
		this.source = referenceRole;
		this.target = targetRole;
		this.symmetric = symmetric;
		this.transitive = transitive;
		this.reflexive = reflexive;
	}

	@Override
	public IResource getSource() {
		return source;
	}

	@Override
	public IResource getTarget() {
		return target;
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
