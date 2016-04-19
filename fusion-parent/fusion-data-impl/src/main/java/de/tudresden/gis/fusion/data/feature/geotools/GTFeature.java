package de.tudresden.gis.fusion.data.feature.geotools;

import java.util.Collection;
import java.util.Set;

import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AbstractFeature;
import de.tudresden.gis.fusion.data.feature.FeatureEntity;
import de.tudresden.gis.fusion.data.feature.FeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.FeatureType;
import de.tudresden.gis.fusion.data.feature.IFeatureConcept;
import de.tudresden.gis.fusion.data.feature.IFeatureEntity;
import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.gis.fusion.data.feature.IFeatureType;
import de.tudresden.gis.fusion.data.literal.WKTLiteral;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ITripleSet;
import de.tudresden.gis.fusion.data.rdf.ObjectSet;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class GTFeature extends AbstractFeature<Feature> implements ITripleSet {
	
	private ObjectSet objectSet;
	
	//predicates
	private IIdentifiableResource RESOURCE_TYPE = RDFVocabulary.TYPE.asResource();
	private IIdentifiableResource WKT = RDFVocabulary.WKT.asResource();
	
	public GTFeature(String identifier, Feature feature, IDataDescription description){
		super(identifier, feature, description);
		objectSet = new ObjectSet();
		//set resource type
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.FEATURE.asResource());
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.GEOMETRY.asResource());
		//set objects
		objectSet.put(WKT, getWKT(feature), true);
	}

	public GTFeature(String identifier, Feature feature){
		this(identifier, feature, null);
	}
	
	public GTFeature(Feature feature){
		this(feature.getIdentifier().getID(), feature, null);
	}
	
	/**
	 * get WKT description of the geometry
	 * @param feature
	 * @return
	 */
	private WKTLiteral getWKT(Feature feature) {
		Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
		return new WKTLiteral(geom.toText());
	}
	
	@Override
	public Feature resolve() {
		return (Feature) super.resolve();
	}
	
	/**
	 * get default geometry of this feature
	 * @return default feature geometry
	 */
	public Geometry getDefaultGeometry(){
		return (Geometry) this.resolve().getDefaultGeometryProperty().getValue();
	}

	@Override
	public IFeatureConcept initConcept(Feature feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeatureType initType(Feature feature) {
		return new FeatureType(feature.getType());
	}

	@Override
	public IFeatureEntity initEntity(Feature feature) {
		return new FeatureEntity(feature.getIdentifier().getID());
	}

	@Override
	public IFeatureRepresentation initRepresentation(Feature feature) {
		return new FeatureRepresentation(feature);
	}

	@Override
	public Collection<IIdentifiableResource> getPredicates() {
		return objectSet.keySet();
	}

	@Override
	public Set<INode> getObject(IIdentifiableResource predicate) {
		return objectSet.get(predicate);
	}

	@Override
	public int size() {
		return objectSet.numberOfObjects();
	}

}
