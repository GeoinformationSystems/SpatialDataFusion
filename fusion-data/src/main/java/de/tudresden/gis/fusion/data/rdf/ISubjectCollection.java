package de.tudresden.gis.fusion.data.rdf;

import java.util.Collection;

public interface ISubjectCollection {
	
	/**
	 * get collection of RDF subjects
	 * @return RDF subject collection
	 */
	public Collection<? extends ISubject> collection();

}
