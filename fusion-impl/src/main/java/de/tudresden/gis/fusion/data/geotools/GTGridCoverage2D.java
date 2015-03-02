package de.tudresden.gis.fusion.data.geotools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.metadata.data.IFeatureDescription;

public class GTGridCoverage2D extends Resource implements ICoverage {

	GridCoverage2D coverage;
	
	public GTGridCoverage2D(IIRI iri, GridCoverage2D coverage) {
		super(iri);
		this.coverage = coverage;
	}
	
	public GTGridCoverage2D(IIRI iri, File file) throws IOException {
		super(iri);
		parseCoverage(file);
	}
	
	@Override
	public String getFeatureId() {
		return getIdentifier().asString();
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
	public IFeatureDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
