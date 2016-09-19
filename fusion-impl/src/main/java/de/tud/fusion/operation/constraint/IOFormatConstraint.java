package de.tud.fusion.operation.constraint;

import java.util.Set;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.description.IOFormat;
import de.tud.fusion.operation.description.IDescriptionConstraint;

public class IOFormatConstraint implements IDescriptionConstraint {

	private Set<IOFormat> supportedFormats;
	
	public IOFormatConstraint(Set<IOFormat> supportedFormats) {
		this.supportedFormats = supportedFormats;
	}

	@Override
	public boolean compliantWith(IDataDescription description) {
		// TODO Auto-generated method stub
		return false;
	}

}
