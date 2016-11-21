package de.tud.fusion.data.rdf;

import java.util.Collection;

import de.tud.fusion.data.description.IDataDescription;

public class Graph extends Subject implements IGraph {
	
	Collection<? extends ISubject> subjects;

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param object data object
	 * @param description object description
	 */
	public Graph(String identifier, Collection<? extends ISubject> object, IDataDescription description) {
		super(identifier, object, description);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends ISubject> getSubjects() {
		return (Collection<? extends ISubject>) resolve();
	}

}
