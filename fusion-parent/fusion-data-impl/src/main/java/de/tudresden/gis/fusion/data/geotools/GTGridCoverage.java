package de.tudresden.gis.fusion.data.geotools;

import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.AFeatureRepresentation;

public class GTGridCoverage extends AFeatureRepresentation {

	private GridCoverage2D coverage;
	
	public GTGridCoverage(IRI identifier, GridCoverage2D coverage){
		super(identifier);
		this.coverage = coverage;
	}
	
	public GTGridCoverage(GridCoverage2D coverage){
		this(new IRI(coverage.toString()), coverage);
	}
	
	public GTGridCoverage(IRI identifier, File file) throws IOException {
		super(identifier);
		AbstractGridFormat format = GridFormatFinder.findFormat(file);
		if(format == null)
			throw new IOException("No applicable coverage reader found");
	    GridCoverage2DReader reader = format.getReader(file);
	    coverage = reader.read(null);
	}
	
	@Override
	public GridCoverage2D value() {
		return coverage;
	}

}
