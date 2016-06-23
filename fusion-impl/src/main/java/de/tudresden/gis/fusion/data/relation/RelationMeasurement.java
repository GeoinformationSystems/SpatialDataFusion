package de.tudresden.gis.fusion.data.relation;

import java.util.Set;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.Subject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

/**
 * relation measurement implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class RelationMeasurement extends AbstractDataResource implements IRelationMeasurement,ISubject {

	/**
	 * measurement subject
	 */
	private Subject subject;
	
	//predicates
	private IResource REFERENCE = RDFVocabulary.RELATION_REFERENCE.getResource();
	private IResource TARGET = RDFVocabulary.RELATION_TARGET.getResource();
	private IResource VALUE = RDFVocabulary.VALUE.getResource();
	private IResource DESCRIPTION = RDFVocabulary.DC_DESCRIPTION.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param reference measurement reference
	 * @param target measurement target
	 * @param value measurement value
	 * @param description measurement description
	 */
	public RelationMeasurement(String identifier, INode reference, INode target, IMeasurement value, IMeasurementDescription description) {
		super(identifier, value, description);
		subject = new Subject(identifier);
		//set resource type
		subject.put(RDFVocabulary.TYPE.getResource(), getType());
		//set objects
		subject.put(REFERENCE, reference, true);
		subject.put(TARGET, target, true);
		subject.put(VALUE, value);
		subject.put(DESCRIPTION, description);
	}
	
	/**
	 * constructor
	 * @param reference measurement reference
	 * @param target measurement target
	 * @param value measurement value
	 * @param description measurement description
	 */
	public RelationMeasurement(INode source, INode target, IMeasurement value, IMeasurementDescription description) {
		this(null, source, target, value, description);
	}
	
	@Override
	public INode getReference() {
		return (IResource) subject.getSingle(REFERENCE);
	}

	@Override
	public INode getTarget() {
		return (IResource) subject.getSingle(TARGET);
	}

	@Override
	public IMeasurement resolve() {
		return (IMeasurement) super.resolve();
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		return resolve().compareTo(measurement);
	}

	@Override
	public IMeasurementDescription getDescription() {
		return (IMeasurementDescription) subject.getSingle(DESCRIPTION);
	}

	@Override
	public Set<IResource> getPredicates() {
		return subject.getPredicates();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) {
		return subject.getObjects(predicate);
	}

	@Override
	public String getValue() {
		return resolve().getValue();
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.RELATION_MEASUREMENT.getResource();
	}

}
