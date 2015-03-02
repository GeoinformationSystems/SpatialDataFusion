package de.tudresden.gis.fusion.data.restrictions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public abstract class IORestriction implements IIORestriction,IRDFTripleSet {
	
	Set<IIdentifiableResource> classification;
	
	@Override
	public IIdentifiableResource getSubject() {
		return new IdentifiableResource(getRestrictionURI());
	}
	
	public abstract String getRestrictionURI();

	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}
	
	@Override
	public IIRI getIdentifier() {
		return this.getSubject().getIdentifier();
	}
	
	@Override
	public Set<IIdentifiableResource> getClassification() {
		initClassification();
		return classification;
	}
	
	private void initClassification() {
		if(classification == null)
			classification = new HashSet<IIdentifiableResource>();
		classification.add(getSubject());
	}

}
