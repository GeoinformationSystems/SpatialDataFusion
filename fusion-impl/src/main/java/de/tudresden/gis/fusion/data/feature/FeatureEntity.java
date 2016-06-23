package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.AbstractDataResource;

/**
 * feature entity implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureEntity extends AbstractDataResource implements IFeatureEntity {
	
	/**
	 * concept defining this entity
	 */
	private IFeatureConcept concept;
	
	/**
	 * representations of this entity
	 */
	private Collection<IFeatureRepresentation> representations;
	
	/**
	 * constructor
	 * @param identifier
	 */
	public FeatureEntity(String identifier){
		super(identifier, identifier);
		if(identifier == null)
			throw new IllegalArgumentException("Entity identifier must not be null");
	}
	
	@Override
	public IFeatureConcept getRelatedConcept() {
		return concept;
	}
	
	@Override
	public Collection<IFeatureRepresentation> getRelatedRepresentations() {
		return representations;
	}
	
	/**
	 * set associated feature concept
	 * @param concept associated concept
	 */
	public void setConcept(IFeatureConcept concept){
		this.concept = concept;
	}
	
	/**
	 * adds an associated feature representation
	 * @param representation associated representation
	 */
	public void addRepresentation(IFeatureRepresentation representation){
		if(representations == null)
			representations = new HashSet<IFeatureRepresentation>();
		representations.add(representation);
	}

}
