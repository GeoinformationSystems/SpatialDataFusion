package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IRI;

public abstract class AFeatureInstance extends AFeatureView implements IFeatureInstance {

	private IFeatureConcept concept;
	private Collection<IFeatureRepresentation> representations;
	
	public AFeatureInstance(IRI identifier, Object object) {
		super(identifier, object, null);
	}
	
	public AFeatureInstance(Object object) {
		this(new IRI(object.toString()), object);
	}
	
	/**
	 * link feature concept
	 * @param concept feature concept
	 */
	public void link(IFeatureConcept concept) {
		if(concept == null)
			return;
		//remove existing concept
		if(this.concept != null)
			super.featureLinks().remove(this.concept);
		//add concept link
		this.concept = concept;
		super.link(concept);
	}
	
	/**
	 * link feature instance
	 * @param instance feature instance
	 */
	public void link(IFeatureRepresentation representation) {
		if(representation == null)
			return;
		//add representation link
		this.representations.add(representation);
		super.link(representation);
	}

	@Override
	public IFeatureConcept concept() {
		return concept;
	}

	@Override
	public Collection<IFeatureRepresentation> representations() {
		return representations;
	}

}
