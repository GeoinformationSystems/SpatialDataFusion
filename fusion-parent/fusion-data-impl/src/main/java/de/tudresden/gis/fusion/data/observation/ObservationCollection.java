package de.tudresden.gis.fusion.data.observation;

import java.util.Collection;
import java.util.Iterator;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IObservation;

public class ObservationCollection extends AbstractDataResource implements IDataCollection<IObservation> {

	public ObservationCollection(String identifier, Collection<IObservation> object, IDataDescription description) {
		super(identifier, object, description);
	}

	@Override
	public Iterator<IObservation> iterator() {
		return resolve().iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IObservation> resolve() {
		return (Collection<IObservation>) super.resolve();
	}

}
