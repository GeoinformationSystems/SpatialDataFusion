package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.feature.relation.IRole;
import de.tudresden.gis.fusion.data.rdf.RDFIdentifiableResource;

public class RelationType extends RDFIdentifiableResource implements IRelationType {
	
	private IRole sourceRole, targetRole;
	private boolean symmetric, transitive, reflexive;
	private Collection<IRelationType> inverse, disjoint;
	private Collection<IRelationMeasurement<?>> measurements;

	public RelationType(IRI identifier, IRole sourceRole, IRole targetRole, boolean symmetric, boolean transitive, boolean reflexive) {
		super(identifier);
		this.sourceRole = sourceRole;
		this.targetRole = targetRole;
		this.symmetric = symmetric;
		this.transitive = transitive;
		this.reflexive = reflexive;
	}

	@Override
	public IRole sourceRole() {
		return sourceRole;
	}

	@Override
	public IRole targetRole() {
		return targetRole;
	}

	@Override
	public boolean symmetric() {
		return symmetric;
	}

	@Override
	public boolean transitive() {
		return transitive;
	}

	@Override
	public boolean reflexive() {
		return reflexive;
	}
	
	/**
	 * add an inverse relation type
	 * @param type inverse relation type
	 */
	public void addInverse(IRelationType type){
		if(inverse == null)
			inverse = new HashSet<IRelationType>();
		this.inverse.add(type);
	}

	@Override
	public Collection<IRelationType> inverseTypes() {
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
	public Collection<IRelationType> disjointTypes() {
		return disjoint;
	}
	
	/**
	 * add underlying relation measurement to this relation type
	 * @param measurement relation measurement
	 */
	public void addMeasurements(IRelationMeasurement<?> measurement){
		if(measurements == null)
			measurements = new HashSet<IRelationMeasurement<?>>();
		this.measurements.add(measurement);
	}

	@Override
	public Collection<IRelationMeasurement<?>> measurements() {
		return measurements;
	}

}
