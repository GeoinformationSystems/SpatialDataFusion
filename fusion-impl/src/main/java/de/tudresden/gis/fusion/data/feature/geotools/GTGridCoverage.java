package de.tudresden.gis.fusion.data.feature.geotools;

import java.io.File;
import java.io.IOException;
import java.util.Set;

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
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.rdf.Subject;

/**
 * GeoTools raster implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTGridCoverage extends AbstractFeature<GridCoverage2D> implements ISubject {
	
	/**
	 * feature subject
	 */
	private Subject subject;

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param coverage GeoTools grid coverage
	 * @param description coverage description
	 */
	public GTGridCoverage(String identifier, GridCoverage2D coverage, IDataDescription description){
		super(identifier, coverage, description);
		initSubject();
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param coverage GeoTools grid coverage
	 */
	public GTGridCoverage(String identifier, GridCoverage2D coverage){
		this(identifier, coverage, null);
	}
	
	/**
	 * constructor
	 * @param coverage GeoTools grid coverage
	 */
	public GTGridCoverage(GridCoverage2D coverage){
		this(coverage.getName().toString(), coverage);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param file grid coverage file for parsing
	 */
	public GTGridCoverage(String identifier, File file) throws IOException {
		super(identifier);
		AbstractGridFormat format = GridFormatFinder.findFormat(file);
		if(format == null)
			throw new IOException("No applicable coverage reader found");
	    GridCoverage2DReader reader = format.getReader(file);
	    super.setObject(reader.read(null));
	    initSubject();
	}
	
	/**
	 * initialize coverage subject
	 */
	private void initSubject() {
		subject = new Subject(this.getIdentifier());
		//set resource type
		subject.put(RDFVocabulary.TYPE.getResource(), RDFVocabulary.COVERAGE.getResource());
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
	
	@Override
	public Set<IResource> getPredicates() {
		return subject.getPredicates();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) {
		return subject.getObjects(predicate);
	}

}
