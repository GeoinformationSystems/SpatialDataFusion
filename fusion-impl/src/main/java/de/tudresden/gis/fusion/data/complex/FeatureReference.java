package de.tudresden.gis.fusion.data.complex;

import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.metadata.IFeatureDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;

public class FeatureReference implements IIdentifiableResource,IFeature {

	private IIRI iri;
	
	public FeatureReference(IIRI iri){
		this.iri = iri;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public boolean isBlank() {
		return iri == null;
	}

	@Override
	public IFeatureDescription getDescription() {
		//TODO: get description
		throw new UnsupportedOperationException("description cannot be resolved");
	}

	@Override
	public Collection<ISpatialProperty> getSpatialProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("spatial properties cannot be resolved");
	}

	@Override
	public Collection<IThematicProperty> getThematicProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("thematic properties cannot be resolved");
	}
	
	@Override
	public Collection<ITemporalProperty> getTemporalProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("temporal properties cannot be resolved");
	}
	
	@Override
	public Map<IIdentifiableResource, INode> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public ISpatialProperty getDefaultSpatialProperty() {
		//TODO: get geometry
		throw new UnsupportedOperationException("default spatial property cannot be resolved");
	}

	@Override
	public IFeatureProperty getFeatureProperty(String identifier) {
		//TODO: get geometry
		throw new UnsupportedOperationException("property cannot be resolved");
	}

}
