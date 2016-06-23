package de.tudresden.gis.fusion.data.observation;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * species measurement
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class SpeciesMeasurement extends IntegerLiteral {

	/**
	 * observes species
	 */
	private IResource species;
	
	/**
	 * observation geometry
	 */
	private Geometry geometry;
	
	/**
	 * constructor
	 * @param species species resource
	 * @param geometry observation geometry
	 * @param value measurement value
	 * @param description measurement description
	 */
	public SpeciesMeasurement(IResource species, Geometry geometry, int value, IMeasurementDescription description) {
		super(value, description);
		this.species = species;
		this.geometry = geometry;
	}

	/**
	 * get observed species
	 * @return species resource
	 */
	public IResource getSpecies() {
		return species;
	}

	/**
	 * get observation measurement geometry
	 * @return observation measurement geometry
	 */
	public Geometry getGeometry() {
		return geometry;
	}

}
