package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IRI;

public abstract class AFeatureConcept extends AFeatureView implements IFeatureConcept {

	private Collection<IFeatureType> types;
	private Collection<IFeatureInstance> instances;
	
	public AFeatureConcept(IRI identifier, Object object) {
		super(identifier, object, null);
	}
	
	public AFeatureConcept(Object object) {
		this(new IRI(object.toString()), object);
	}
	
	/**
	 * link feature type
	 * @param concept feature concept
	 */
	public void link(IFeatureType type) {
		if(type == null)
			return;
		//add concept link
		this.types.add(type);
		super.link(type);
	}
	
	/**
	 * link feature instance
	 * @param instance feature instance
	 */
	public void link(IFeatureInstance instance) {
		if(instance == null)
			return;
		//add instance link
		this.instances.add(instance);
		super.link(instance);
	}

	@Override
	public Collection<IFeatureType> types() {
		return types;
	}

	@Override
	public Collection<IFeatureInstance> instances() {
		return instances;
	}

}
