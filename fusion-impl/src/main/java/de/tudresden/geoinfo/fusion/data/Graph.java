package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Collection;
import java.util.Iterator;

public class Graph<T extends ISubject> extends Subject implements IGraph<T> {

	/**
	 * constructor
	 * @param sIdentifier resource identifier
	 * @param collection subject collection
	 * @param description object description
	 */
	public Graph(IIdentifier sIdentifier, Collection<T> collection, IMetadataForData description) {
		super(sIdentifier, collection, description);
		put(Predicates.TYPE.getResource(), Objects.BAG.getResource());
	}

	@SuppressWarnings("unchecked")
    @Override
	public Collection<T> resolve() {
		return (Collection<T>) super.resolve();
	}

    @Override
    public Iterator<T> iterator() {
        return resolve().iterator();
    }
}
