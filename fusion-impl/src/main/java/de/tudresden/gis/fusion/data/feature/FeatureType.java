package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.AbstractDataResource;

/**
 * feature type implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureType extends AbstractDataResource implements IFeatureType {
	
	/**
	 * concept described by this type
	 */
	private IFeatureConcept concept;
	
	/**
	 * representations implementing this type
	 */
	private Collection<IFeatureRepresentation> representations;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param type feature type object
	 */
	public FeatureType(String identifier, Object type){
		super(identifier, type);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public FeatureType(String identifier){
		super(identifier, null);
	}
	
	/**
	 * constructor
	 * @param type feature type object
	 */
	public FeatureType(Object type){
		this(null, type);
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
	 * @param concept associated concept
	 */
	public void setConcept(IFeatureConcept concept){
		this.concept = concept;
	}
	
	/**
	 * adds a feature representation
	 * @param representation associated representation
	 */
	public void addRepresentation(IFeatureRepresentation representation){
		if(representations == null)
			representations = new HashSet<IFeatureRepresentation>();
		representations.add(representation);
	}

}
