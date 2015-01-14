package de.tudresden.gis.fusion.data.metadata;

import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IIRI;

public interface IMeasurementDescription extends IDataDescription {
	
	public IIRI getProcessIRI();
	
	public IRelationType getRelationType();
	
	public IMeasurementRange<?> getMeasurementRange();
	
}
