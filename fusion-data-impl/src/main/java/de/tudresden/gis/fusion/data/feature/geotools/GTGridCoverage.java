package de.tudresden.gis.fusion.data.feature.geotools;

import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AbstractFeature;
import de.tudresden.gis.fusion.data.feature.FeatureEntity;
import de.tudresden.gis.fusion.data.feature.FeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;

public class GTGridCoverage extends AbstractFeature<GridCoverage2D> {

	public GTGridCoverage(String identifier, GridCoverage2D coverage, IDataDescription description){
		super(identifier, coverage, description);
	}
	
	public GTGridCoverage(String identifier, GridCoverage2D coverage){
		super(identifier, coverage);
	}
	
	public GTGridCoverage(GridCoverage2D coverage){
		this(coverage.getName().toString(), coverage);
	}
	
	public GTGridCoverage(String identifier, File file) throws IOException {
		super(identifier);
		AbstractGridFormat format = GridFormatFinder.findFormat(file);
		if(format == null)
			throw new IOException("No applicable coverage reader found");
	    GridCoverage2DReader reader = format.getReader(file);
	    super.setObject(reader.read(null));
	}
	
	@Override
	public GridCoverage2D resolve() {
		return (GridCoverage2D) super.resolve();
	}

	@Override
	public IFeatureConcept initConcept(GridCoverage2D feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureType initType(GridCoverage2D feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureEntity initEntity(GridCoverage2D feature) {
		return new FeatureEntity(feature.getName().toString());
	}

	@Override
	public IFeatureRepresentation initRepresentation(GridCoverage2D feature) {
		return new FeatureRepresentation(feature);
	}

}
