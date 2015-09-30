package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class IODescription extends Resource implements IIODataDescription {
	
	private String title, description;
	private Collection<IDataConstraint> constraints;
	
	public IODescription(String identifier, String title, String description, Collection<IDataConstraint> constraints){
		super(identifier);
		this.title = title;
		this.description = description;
		this.constraints = constraints;
	}
	
	public IODescription(String title, String abstrakt, Collection<IDataConstraint> constraints){
		this(null, title, abstrakt, constraints);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
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
