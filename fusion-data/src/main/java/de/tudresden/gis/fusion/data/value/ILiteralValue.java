package de.tudresden.gis.fusion.data.value;

import de.tudresden.gis.fusion.data.IDataValue;
import de.tudresden.gis.fusion.data.type.ILiteralType;

public interface ILiteralValue extends IDataValue {

	@Override
	public ILiteralType getType();
	
}
