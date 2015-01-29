package de.tudresden.gis.fusion.metadata;

import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.metadata.IMeasurementRange;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.manage.DataUtilities;

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
	
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(EFusionNamespace.HAS_PROCESS_URI.resource(), DataUtilities.toSet(new IdentifiableResource(getProcessIRI())));
		objectSet.put(EFusionNamespace.HAS_RELATION_TYPE.resource(), DataUtilities.toSet(getRelationType()));
		objectSet.put(EFusionNamespace.HAS_RANGE.resource(), DataUtilities.toSet(getMeasurementRange()));
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
