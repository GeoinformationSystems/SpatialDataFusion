package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.operation.io.IFilter;

public interface IDataRetrieval extends IOperation {
	
	public void setFilter(IFilter filter);

}
