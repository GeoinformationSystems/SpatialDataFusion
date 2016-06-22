package de.tudresden.gis.fusion.operation.description;

import java.util.Arrays;
import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;

public class InputDescription extends IODescription implements IInputDescription {

	private IData defaultInput;
	
	public InputDescription(String identifier, String title, String abstrakt, Collection<IDataConstraint> constraints, IData defaultInput) {
		super(identifier, title, abstrakt, constraints);
		this.defaultInput = defaultInput;
	}
	
	public InputDescription(String identifier, String title, String abstrakt, IDataConstraint[] constraints, IData defaultInput) {
		this(identifier, title, abstrakt, Arrays.asList(constraints), defaultInput);
	}
	
	public InputDescription(String identifier, String title, String abstrakt, Collection<IDataConstraint> constraints) {
		this(identifier, title, abstrakt, constraints, null);
	}
	
	public InputDescription(String identifier, String title, String abstrakt, IDataConstraint[] constraints) {
		this(identifier, title, abstrakt, constraints, null);
	}

	@Override
	public IData getDefault() {
		return defaultInput;
	}

}
