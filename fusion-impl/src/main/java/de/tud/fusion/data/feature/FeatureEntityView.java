package de.tud.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * feature entity implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureEntityView extends ResourceData implements IFeatureEntityView {
	
	/**
	 * concept defining this entity
	 */
	private IFeatureConceptView concept;
	
	/**
	 * representations of this entity
	 */
	private Collection<IFeatureRepresentationView> representations;
	
	/**
	 * constructor
	 * @param identifier
	 */
	public FeatureEntityView(String identifier, Object entity, IDataDescription description) {
		super(identifier, entity, description);
		if(identifier == null)
			throw new IllegalArgumentException("Entity identifier must not be null");
	}
	
	@Override
	public IFeatureConceptView getRelatedConcept() {
		return concept;
	}
	
	@Override
	public Collection<IFeatureRepresentationView> getRelatedRepresentations() {
		return representations;
	}
	
	/**
	 * set associated feature concept
	 * @param concept associated concept
	 */
	public void setConcept(IFeatureConceptView concept){
		this.concept = concept;
	}
	
	/**
	 * adds an associated feature representation
	 * @param representation associated representation
	 */
	public void addRepresentation(IFeatureRepresentationView representation){
		if(representations == null)
			representations = new HashSet<IFeatureRepresentationView>();
		representations.add(representation);
	}

}
