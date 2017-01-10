package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureRepresentation;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * GeoTools raster implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTGridRepresentation extends AbstractFeatureRepresentation {

	private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
    private static IResource TYPE_REPRESENTATION = Objects.FEATURE_REPRESENTATION.getResource();
	private static IResource TYPE_COVERAGE = Objects.COVERAGE.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param coverage GeoTools grid coverage
	 * @param metadata coverage description
	 */
	public GTGridRepresentation(IIdentifier identifier, GridCoverage2D coverage, IMetadataForData metadata){
		super(identifier, coverage, metadata);
		initSubject();
	}
	
	/**
	 * initialize coverage subject
	 */
	private void initSubject() {
		//set resource type
		put(PREDICATE_TYPE, TYPE_REPRESENTATION);
        put(PREDICATE_TYPE, TYPE_COVERAGE);
	}
	
	@Override
	public GridCoverage2D resolve() {
		return (GridCoverage2D) super.resolve();
	}
	


    @Override
    public Object getProperty(IIdentifier identifier) {
        return null;
    }

    @Override
    public Object getDefaultGeometry() {
        return null;
    }

    @Override
    public Envelope getBounds() {
        return this.resolve().getEnvelope();
    }

    @Override
    public CoordinateReferenceSystem getReferenceSystem() {
        return this.resolve().getCoordinateReferenceSystem();
    }
}
