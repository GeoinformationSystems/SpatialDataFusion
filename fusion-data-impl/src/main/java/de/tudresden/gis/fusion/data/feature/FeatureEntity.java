package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.AbstractDataResource;

public class FeatureEntity extends AbstractDataResource implements IFeatureEntity {
	
	private IFeatureConcept concept;
	private Collection<IFeatureRepresentation> representations;
	
	public FeatureEntity(String identifier){
		super(identifier, identifier);
		if(identifier == null)
			throw new IllegalArgumentException("Entity identifier must not be null.");
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
	 * set feature concept
	 * @param concept input concept
	 */
	public void setConcept(IFeatureConcept concept){
		this.concept = concept;
	}
	
	/**
	 * adds a feature representation
	 * @param representation input representation
	 */
	public void addRepresentation(IFeatureRepresentation representation){
		if(representations == null)
			representations = new HashSet<IFeatureRepresentation>();
		representations.add(representation);
	}

}
