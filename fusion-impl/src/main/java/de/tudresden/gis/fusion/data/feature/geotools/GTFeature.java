package de.tudresden.gis.fusion.data.feature.geotools;

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
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.Subject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

/**
 * GeoTools feature implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeature extends AbstractFeature<Feature> implements ISubject {
	
	/**
	 * feature subject
	 */
	private Subject subject;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param feature feature object
	 * @param description feature description
	 */
	public GTFeature(String identifier, Feature feature, IDataDescription description){
		super(identifier, feature, description);
		subject = new Subject(identifier);
		//set resource type
		subject.put(RDFVocabulary.TYPE.getResource(), RDFVocabulary.FEATURE.getResource());
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param feature feature object
	 */
	public GTFeature(String identifier, Feature feature){
		this(identifier, feature, null);
	}
	
	/**
	 * constructor
	 * @param feature feature object
	 */
	public GTFeature(Feature feature){
		this(feature.getIdentifier().getID(), feature, null);
	}
	
	/**
	 * get WKT description of the geometry
	 * @param feature
	 * @return
	 */
	public WKTLiteral getWKTGeometry() {
		return new WKTLiteral(getDefaultGeometry().toText());
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
	public Set<IResource> getPredicates() {
		return subject.getPredicates();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) {
		return subject.getObjects(predicate);
	}

	/**
	 * get number of associated objects
	 * @return object count
	 */
	public int size() {
		return subject.getNumberOfObjects();
	}
	
	/**
	 * add geometry as WKT to RDF representation (caution: large geometries are likely to cause memory issues)
	 */
	public void addWKTGeometry(){
		subject.put(RDFVocabulary.WKT.getResource(), getWKTGeometry(), true);
	}

}
