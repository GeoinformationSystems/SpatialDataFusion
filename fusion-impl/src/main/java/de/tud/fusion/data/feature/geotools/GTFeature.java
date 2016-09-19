package de.tud.fusion.data.feature.geotools;

import java.util.Set;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.feature.FeatureEntityView;
import de.tud.fusion.data.feature.FeatureRepresentationView;
import de.tud.fusion.data.feature.FeatureTypeView;
import de.tud.fusion.data.feature.AbstractFeature;
import de.tud.fusion.data.feature.IFeatureConceptView;
import de.tud.fusion.data.feature.IFeatureEntityView;
import de.tud.fusion.data.feature.IFeatureRepresentationView;
import de.tud.fusion.data.feature.IFeatureTypeView;
import de.tud.fusion.data.literal.WKTLiteral;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.data.relation.IFeatureRelation;

/**
 * GeoTools feature implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeature extends AbstractFeature {
	
	private final IResource PREDICATE_WKT = RDFVocabulary.WKT.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param feature feature object
	 * @param description feature description
	 * @param relations feature relations
	 */
	public GTFeature(String identifier, SimpleFeature feature, IDataDescription description, Set<IFeatureRelation> relations){
		super(identifier, feature, description, relations);
	}
	
	/**
	 * get GT feature from abstract feature
	 * @param feature GT feature instance
	 */
	public GTFeature(AbstractFeature feature){
		super(feature.getIdentifier(), (Feature) feature.getRepresentation().resolve(), feature.getDescription(), feature.getRelations());
	}

	@Override
	public SimpleFeature resolve() {
		return (SimpleFeature) super.resolve();
	}
	
	/**
	 * get WKT description of the feature geometry
	 * @return WKT geometry of the feature
	 */
	public WKTLiteral getWKTGeometry() {
		return new WKTLiteral(getDefaultGeometry().toText());
	}
	
	/**
	 * get default geometry of this feature
	 * @return default feature geometry
	 */
	public Geometry getDefaultGeometry(){
		return (Geometry) this.resolve().getDefaultGeometryProperty().getValue();
	}
	
	/**
	 * add geometry as WKT to RDF representation (caution: large geometries are likely to cause memory issues)
	 */
	public void showWKTGeometry(){
		put(PREDICATE_WKT, getWKTGeometry());
	}
	
	/**
	 * hide geometry as WKT in RDF representation
	 */
	public void hideWKTGeometry(){
		remove(PREDICATE_WKT);
	}
	
	@Override
	public IFeatureRepresentationView initRepresentation() {
		return new FeatureRepresentationView(null, resolve(), null);
	}

	@Override
	public IFeatureEntityView initEntity() {
		return new FeatureEntityView(resolve().getID(), resolve().getID(), null);
	}

	@Override
	public IFeatureTypeView initType() {
		return new FeatureTypeView(resolve().getFeatureType().getTypeName(), resolve().getFeatureType(), null);
	}

	@Override
	public IFeatureConceptView initConcept() {
		// TODO Auto-generated method stub
		return null;
	}

}
