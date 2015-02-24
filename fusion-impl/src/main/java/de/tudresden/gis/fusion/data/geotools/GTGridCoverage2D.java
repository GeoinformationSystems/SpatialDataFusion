package de.tudresden.gis.fusion.data.geotools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.metadata.IFeatureDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;

public class GTGridCoverage2D implements IIdentifiableResource,ICoverage {

	private IIRI iri;
	GridCoverage2D coverage;
	
	public GTGridCoverage2D(IIRI identifier, GridCoverage2D coverage) {
		this.coverage = coverage;
		this.iri = identifier;
	}
	
	public GTGridCoverage2D(IIRI identifier, File file) throws IOException {
		parseCoverage(file);
		this.iri = identifier;
	}
	
	private void parseCoverage(File file) throws IOException {
		AbstractGridFormat format = GridFormatFinder.findFormat(file);
		if(format == null)
			throw new IOException("No applicable coverage reader found.");
	    GridCoverage2DReader reader = format.getReader(file);
	    coverage = reader.read(null);
	}
	
	public GridCoverage2D getCoverage(){
		return coverage;
	}
	
	@Override
	public boolean isBlank() {
		return false;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

	@Override
	public IFeatureDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IIdentifiableResource, Set<INode>> getObjectSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
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
	public Collection<ISpatialProperty> getSpatialProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("spatial properties cannot be resolved");
	}

	@Override
	public ISpatialProperty getDefaultSpatialProperty() {
		//TODO: get properties
		throw new UnsupportedOperationException("default spatial property cannot be resolved");
	}

	@Override
	public IFeatureProperty getFeatureProperty(String identifier) {
		//TODO: get properties
		throw new UnsupportedOperationException("feature properties cannot be resolved");
	}

}
