package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.*;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * GeoTools feature implementation
 */
public class GTGridFeature extends AbstractFeature {

    /**
     * constructor
     * @param identifier resource identifier
     * @param feature GT grid object
     * @param description feature description
     * @param relations feature relations
     */
    public GTGridFeature(IIdentifier identifier, GridCoverage2D feature, IMetadataForData description, Set<IRelation<? extends IFeature>> relations){
        super(identifier, feature, description, relations);
    }

    /**
     * constructor
     * @param identifier resource identifier
     * @param file coverage file
     * @param description feature description
     * @param relations feature relations
     */
    public GTGridFeature(IIdentifier identifier, File file, IMetadataForData description, Set<IRelation<? extends IFeature>> relations) throws IOException {
        this(identifier, getCoverage(file), description, relations);
    }

    @Override
    public GridCoverage2D resolve(){
        return (GridCoverage2D) super.resolve();
    }

	@Override
	public AbstractFeatureRepresentation initRepresentation() {
	    return new GTGridRepresentation(new Identifier(resolve().toString()), resolve(), null);
	}

	@Override
	public AbstractFeatureEntity initEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureType initType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureConcept initConcept() {
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
