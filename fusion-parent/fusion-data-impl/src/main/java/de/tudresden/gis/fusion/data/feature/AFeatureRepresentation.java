package de.tudresden.gis.fusion.data.feature;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;

public abstract class AFeatureRepresentation extends AFeatureView implements IFeatureRepresentation {

	private IFeatureType type;
	private IFeatureInstance instance;
	
	public AFeatureRepresentation(IRI identifier, Object object, IDataDescription description) {
		super(identifier, object, description);
	}
	
	public AFeatureRepresentation(Object object) {
		this(new IRI(object.toString()), object, null);
	}
	
	/**
	 * link feature type
	 * @param type feature type
	 */
	public void link(IFeatureType type) {
		if(type == null)
			return;
		//remove existing type
		if(this.type != null)
			super.featureLinks().remove(this.type);
		//add type link
		this.type = type;
		super.link(type);
	}
	
	/**
	 * link feature instance
	 * @param instance feature instance
	 */
	public void link(IFeatureInstance instance) {
		if(instance == null)
			return;
		//remove existing instance
		if(this.instance != null)
			super.featureLinks().remove(this.instance);
		//add instance link
		this.instance = instance;
		super.link(instance);
	}

	@Override
	public IFeatureType type() {
		return type;
	}

	@Override
	public IFeatureInstance instance() {
		return instance;
	}

}
