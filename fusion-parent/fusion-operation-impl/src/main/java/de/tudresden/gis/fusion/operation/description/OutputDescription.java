package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class OutputDescription extends IODescription implements IOutputDescription {

	public OutputDescription(String title, String abstrakt, Collection<IDataConstraint> constraints) {
		super(title, abstrakt, constraints);
	}

}
