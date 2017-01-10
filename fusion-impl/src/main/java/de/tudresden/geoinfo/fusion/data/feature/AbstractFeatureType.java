package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Collection;
import java.util.HashSet;

/**
 * feature type implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractFeatureType extends Subject implements IFeatureType {

	private IFeatureConcept concept;
	private Collection<IFeatureRepresentation> representations;
	
	/**
     * constructor
     * @param identifier resource identifier
     * @param type feature type object
     */
    public AbstractFeatureType(IIdentifier identifier, Object type, IMetadataForData description){
        super(identifier, type, description);
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
	public void setRelatedConcept(IFeatureConcept concept){
		this.concept = concept;
	}
	
	/**
	 * adds a feature representation
	 * @param representation associated representation
	 */
	public void addRelatedRepresentation(IFeatureRepresentation representation){
		if(representations == null)
			representations = new HashSet<>();
		representations.add(representation);
	}
	
	@Override
	public boolean equals(Object type){
		return type instanceof AbstractFeatureType && this.resolve().equals(((AbstractFeatureType) type).resolve());
	}

}
