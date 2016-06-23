package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;

public class ProcessDescription extends Resource implements IProcessDescription {
	
	String title, description;
	private Collection<IProcessConstraint> constraints;
	
	public ProcessDescription(String identifier, String title, String description, Collection<IProcessConstraint> constraints){
		super(identifier);
		this.title = title;
		this.description = description;
		this.constraints = constraints;
	}

	@Override
	public Collection<IProcessConstraint> constraints() {
		return constraints;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
