package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.*;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Set;

/**
 * GeoTools feature implementation
 */
public class GTVectorFeature extends AbstractFeature {
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param feature GT vector object
	 * @param description feature description
	 * @param relations feature relations
	 */
	public GTVectorFeature(IIdentifier identifier, SimpleFeature feature, IMetadataForData description, Set<IRelation<? extends IFeature>> relations){
		super(identifier, feature, description, relations);
	}

    @Override
    public SimpleFeature resolve(){
        return (SimpleFeature) super.resolve();
    }

	@Override
	public AbstractFeatureRepresentation initRepresentation() {
	    return new GTVectorRepresentation(new Identifier((resolve()).getID()), resolve(), null);
	}

	@Override
	public AbstractFeatureEntity initEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractFeatureType initType() {
        return new GTFeatureType(new Identifier((resolve()).getFeatureType().getTypeName()), (resolve()).getFeatureType(), null);
	}

	@Override
	public AbstractFeatureConcept initConcept() {
		// TODO Auto-generated method stub
		return null;
	}

}
