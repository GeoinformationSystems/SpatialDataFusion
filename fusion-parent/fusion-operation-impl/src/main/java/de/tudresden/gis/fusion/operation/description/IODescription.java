package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class IODescription extends RDFResource implements IIODataDescription {
	
	private String title, abstrakt;
	private Collection<IDataConstraint> constraints;
	
	public IODescription(IRI identifier, String title, String abstrakt, Collection<IDataConstraint> constraints){
		super(identifier);
		this.title = title;
		this.abstrakt = abstrakt;
		this.constraints = constraints;
	}
	
	public IODescription(String title, String abstrakt, Collection<IDataConstraint> constraints){
		this(null, title, abstrakt, constraints);
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String abstrakt() {
		return abstrakt;
	}
	
	/**
	 * add constraint to data description
	 * @param constraint data constraint
	 */
	public void addConstraint(IDataConstraint constraint){
		if(constraints == null)
			constraints = new HashSet<IDataConstraint>();
		constraints.add(constraint);
	}

	@Override
	public Collection<IDataConstraint> constraints() {
		return constraints;
	}

}
