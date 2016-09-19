package de.tud.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * feature type implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureTypeView extends ResourceData implements IFeatureTypeView {
	
	/**
	 * concept described by this type
	 */
	private IFeatureConceptView concept;
	
	/**
	 * representations implementing this type
	 */
	private Collection<IFeatureRepresentationView> representations;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param type feature type object
	 */
	public FeatureTypeView(String identifier, FeatureType type, IDataDescription description){
		super(identifier, type, description);
	}
	
	@Override
	public Set<String> getPropertyIdentifier() {
		Set<String> identifiers = new HashSet<String>();
		for(PropertyDescriptor property : ((FeatureType) resolve()).getDescriptors()){
			identifiers.add(property.getName().getLocalPart());
		}
		return identifiers;
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
	 * set feature concept
	 * @param concept associated concept
	 */
	public void setConcept(IFeatureConceptView concept){
		this.concept = concept;
	}
	
	/**
	 * adds a feature representation
	 * @param representation associated representation
	 */
	public void addRepresentation(IFeatureRepresentationView representation){
		if(representations == null)
			representations = new HashSet<IFeatureRepresentationView>();
		representations.add(representation);
	}
	
	@Override
	public boolean equals(Object type){
		return type instanceof FeatureTypeView ? this.resolve().equals(((FeatureTypeView) type).resolve()) : false;
	}

}
