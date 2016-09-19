package de.tud.fusion.data.relation;

import de.tud.fusion.data.IMeasurement;
import de.tud.fusion.data.description.IMeasurementDescription;
import de.tud.fusion.data.feature.IFeature;
import de.tud.fusion.data.literal.StringLiteral;
import de.tud.fusion.data.rdf.INode;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.data.rdf.Subject;

/**
 * relation measurement implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class RelationMeasurement extends Subject implements IRelationMeasurement {
	
	//predicates
	private IResource REFERENCE = RDFVocabulary.RELATION_REFERENCE.getResource();
	private IResource TARGET = RDFVocabulary.RELATION_TARGET.getResource();
	private IResource VALUE = RDFVocabulary.VALUE.getResource();
	private IResource DESCRIPTION = RDFVocabulary.DC_DESCRIPTION.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param reference reference feature
	 * @param target target feature
	 * @param value measurement value
	 * @param description measurement description
	 */
	public RelationMeasurement(String identifier, INode reference, INode target, IMeasurement value, IMeasurementDescription description) {
		super(identifier, value, description);
		//set resource type
		put(RDFVocabulary.TYPE.getResource(), getType());
		//set objects
		put(REFERENCE, reference);
		put(TARGET, target);
		put(VALUE, value);
		put(DESCRIPTION, new StringLiteral(description.getDescription()));
	}
	
	@Override
	public IFeature getReference() {
		return (IFeature) getObject(REFERENCE);
	}

	@Override
	public IFeature getTarget() {
		return (IFeature) getObject(TARGET);
	}

	@Override
	public IMeasurement resolve() {
		return (IMeasurement) super.resolve();
	}

	@Override
	public String getValue() {
		return resolve().getValue();
	}

	@Override
	public IResource getType() {
		return RDFVocabulary.RELATION_MEASUREMENT.getResource();
	}

	@Override
	public IMeasurementDescription getDescription() {
		return (IMeasurementDescription) super.getDescription();
	}

	@Override
	public int compareTo(IMeasurement o) {
		return resolve().compareTo(o);
	}

}
