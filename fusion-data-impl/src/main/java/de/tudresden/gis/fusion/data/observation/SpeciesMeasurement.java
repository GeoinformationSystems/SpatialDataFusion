package de.tudresden.gis.fusion.data.observation;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public class SpeciesMeasurement extends IntegerLiteral {

	private IIdentifiableResource species;
	private Geometry geometry;
	
	public SpeciesMeasurement(IIdentifiableResource species, Geometry geometry, int value, IMeasurementDescription description) {
		super(value, description);
		this.species = species;
		this.geometry = geometry;
	}

	public IIdentifiableResource getSpecies() {
		return species;
	}

	public Geometry getGeometry() {
		return geometry;
	}

}
