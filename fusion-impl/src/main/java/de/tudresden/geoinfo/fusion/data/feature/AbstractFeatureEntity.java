package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Collection;
import java.util.HashSet;

/**
 * feature entity implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractFeatureEntity extends Subject implements IFeatureEntity {

	private IFeatureConcept concept;
	private Collection<IFeatureRepresentation> representations;
	
	/**
	 * constructor
	 * @param identifier entity identifier
	 */
	public AbstractFeatureEntity(IIdentifier identifier, Object entity, IMetadataForData description) {
		super(identifier, entity, description);
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
	public void setRelatedConcept(IFeatureConcept concept){
		this.concept = concept;
	}
	
	/**
	 * adds an associated feature representation
	 * @param representation associated representation
	 */
	public void addRelatedRepresentation(IFeatureRepresentation representation){
		if(representations == null)
			representations = new HashSet<>();
		representations.add(representation);
	}

}
