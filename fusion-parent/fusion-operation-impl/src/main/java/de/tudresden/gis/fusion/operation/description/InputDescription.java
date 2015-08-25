package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class InputDescription extends IODescription implements IInputDescription {

	private IData defaultInput;
	
	public InputDescription(String title, String abstrakt, Collection<IDataConstraint> constraints, IData defaultInput) {
		super(title, abstrakt, constraints);
		this.defaultInput = defaultInput;
	}

	@Override
	public IData getDefault() {
		return defaultInput;
	}

}
