package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.feature.relation.IRole;
import de.tudresden.gis.fusion.data.rdf.Resource;

public class RelationType extends Resource implements IRelationType {
	
	private IRole sourceRole, targetRole;
	private boolean symmetric, transitive, reflexive;
	private IRelationType inverse;
	private Collection<IRelationType> disjoint;

	public RelationType(String identifier, IRole sourceRole, IRole targetRole, boolean symmetric, boolean transitive, boolean reflexive) {
		super(identifier);
		this.sourceRole = sourceRole;
		this.targetRole = targetRole;
		this.symmetric = symmetric;
		this.transitive = transitive;
		this.reflexive = reflexive;
	}

	@Override
	public IRole getSource() {
		return sourceRole;
	}

	@Override
	public IRole getTarget() {
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
	
	/**
	 * set inverse relation type
	 * @param inverse inverse relation type
	 */
	public void addInverse(IRelationType inverse){
		this.inverse = inverse;
	}

	@Override
	public IRelationType getInverse() {
		return inverse;
	}
	
	/**
	 * add a disjoint relation type
	 * @param type disjoint relation type
	 */
	public void addDisjoint(IRelationType type){
		if(disjoint == null)
			disjoint = new HashSet<IRelationType>();
		this.disjoint.add(type);
	}

	@Override
	public Collection<IRelationType> getDisjoint() {
		return disjoint;
	}

}
