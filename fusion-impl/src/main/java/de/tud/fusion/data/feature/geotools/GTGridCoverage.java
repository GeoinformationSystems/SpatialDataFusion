package de.tud.fusion.data.feature.geotools;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.feature.FeatureEntityView;
import de.tud.fusion.data.feature.FeatureRepresentationView;
import de.tud.fusion.data.feature.FeatureTypeView;
import de.tud.fusion.data.feature.AbstractFeature;
import de.tud.fusion.data.feature.IFeature;
import de.tud.fusion.data.feature.IFeatureConceptView;
import de.tud.fusion.data.feature.IFeatureEntityView;
import de.tud.fusion.data.feature.IFeatureRepresentationView;
import de.tud.fusion.data.feature.IFeatureTypeView;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.data.relation.IFeatureRelation;

/**
 * GeoTools raster implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTGridCoverage extends AbstractFeature implements IFeature {

	public static final String PROPERTY_GEOMETRY = "coverage";
	
	private final IResource PREDICATE_TYPE = RDFVocabulary.TYPE.getResource();
	private final IResource TYPE_COVERAGE = RDFVocabulary.COVERAGE.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param coverage GeoTools grid coverage
	 * @param description coverage description
	 * @param relations feature relations
	 */
	public GTGridCoverage(String identifier, GridCoverage2D coverage, IDataDescription description, Set<IFeatureRelation> relations){
		super(identifier, coverage, description, relations);
		initSubject();
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param file grid coverage file for parsing
	 */
	public GTGridCoverage(String identifier, File file, IDataDescription description, Set<IFeatureRelation> relations) throws IOException {
		this(identifier, getCoverage(file), description, relations);
	}
	
	/**
	 * initialize coverage subject
	 */
	private void initSubject() {
		//set resource type
		put(PREDICATE_TYPE, TYPE_COVERAGE);
	}
	
	@Override
	public GridCoverage2D resolve() {
		return (GridCoverage2D) super.resolve();
	}
	
	@Override
	public IFeatureRepresentationView initRepresentation() {
		return new FeatureRepresentationView(null, resolve(), null);
	}

	@Override
	public IFeatureEntityView initEntity() {
		return new FeatureEntityView(null, resolve().getName().toString(), null);
	}

	@Override
	public IFeatureTypeView initType() {
		return new FeatureTypeView(null, null, null);
	}

	@Override
	public IFeatureConceptView initConcept() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * get coverage from file
	 * @param file input file
	 * @return GeoTools coverage object
	 * @throws IOException
	 */
	public static GridCoverage2D getCoverage(File file) throws IOException {
		AbstractGridFormat format = GridFormatFinder.findFormat(file);
		if(format == null)
			throw new IOException("No applicable coverage reader found");
	    GridCoverage2DReader reader = format.getReader(file);
	    return reader.read(null);
	}

}
