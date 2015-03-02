package de.tudresden.gis.fusion.data.complex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.metadata.data.IFeatureDescription;

public class FeatureReference extends Resource implements IRDFTripleSet,IFeature {

	public FeatureReference(IIRI iri){
		super(iri);
	}
	
	public FeatureReference(String sIri){
		super(new IRI(sIri));
	}
	
	@Override
	public String getFeatureId() {
		return getIdentifier().asString();
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
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return new IdentifiableResource(getIdentifier());
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

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

}
