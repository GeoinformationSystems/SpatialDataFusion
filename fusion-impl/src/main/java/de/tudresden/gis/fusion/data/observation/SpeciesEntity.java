package de.tudresden.gis.fusion.data.observation;

import de.tudresden.gis.fusion.data.feature.FeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;

/**
 * individual feature entity
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class SpeciesEntity extends FeatureEntity implements IFeatureEntity {

	/**
	 * constructor
	 * @param identifier entity identifier
	 */
	public SpeciesEntity(String identifier) {
		super(identifier);
	}
	
}
