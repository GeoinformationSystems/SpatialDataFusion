package de.tudresden.gis.fusion.operation.description;

import java.util.Arrays;
import java.util.Collection;

import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class OutputDescription extends IODescription implements IOutputDescription {

	public OutputDescription(String identifier, String title, String abstrakt, Collection<IDataConstraint> constraints) {
		super(identifier, title, abstrakt, constraints);
	}
	
	public OutputDescription(String identifier, String title, String abstrakt, IDataConstraint[] constraints) {
		this(identifier, title, abstrakt, Arrays.asList(constraints));
	}

}
