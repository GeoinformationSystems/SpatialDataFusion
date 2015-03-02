package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IMeasurementDescription;

public class MeasurementDescription extends DataDescription implements IMeasurementDescription,IRDFTripleSet {

	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_MEASUREMENT_DESCRIPTION.resource();
	private final IIdentifiableResource RANGE = EFusionNamespace.MEASUREMENT_DESCRIPTION_HAS_RANGE.resource();
	
	private MeasurementRange<?> range;
	
	public MeasurementDescription(IIRI iri, String description, MeasurementRange<?> range) {
		super(iri, description);
		this.range = range;
	}
	
	@SuppressWarnings("rawtypes")
	public MeasurementDescription(IRDFTripleSet decodedRDFResource) throws IOException {
		//set iri
		super(decodedRDFResource);
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		//set range
		INode nRange = DataUtilities.getSingleFromObjectSet(objectSet, RANGE, IRDFTripleSet.class, true);
		this.range = new MeasurementRange((IRDFTripleSet) nRange);
	}

	@Override
	public MeasurementRange<?> getMeasurementRange() {
		return range;
	}
	
	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		objectSet.put(RANGE, DataUtilities.toSet(getMeasurementRange()));
		return objectSet;
	}

}
