package de.tudresden.gis.fusion.metadata;

import java.util.Map;

import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.metadata.IMeasurementRange;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public class MeasurementDescription extends DataDescription implements IMeasurementDescription {

	private IRelationType relationType;
	private IMeasurementRange<?> range;
	private IIRI iri;
	
	public MeasurementDescription(IIRI iri, String description, IRelationType relationType, IMeasurementRange<?> range) {
		super(description);
		this.iri = iri;
		this.relationType = relationType;
		this.range = range;
	}

	@Override
	public IMeasurementRange<?> getMeasurementRange() {
		return range;
	}
	
	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = super.getObjectSet();
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_PROCESS_URI.asString()), new IdentifiableResource(getProcessIRI()));
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_RELATION_TYPE.asString()), getRelationType());
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_RANGE.asString()), getMeasurementRange());
		return objectSet;
	}

	@Override
	public IRelationType getRelationType() {
		return relationType;
	}

	@Override
	public IIRI getProcessIRI() {
		return iri;
	}

}
