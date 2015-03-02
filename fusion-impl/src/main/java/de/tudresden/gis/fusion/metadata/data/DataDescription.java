package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.ERDFNamespaces;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IDescription;

public class DataDescription extends Resource implements IDescription,IRDFTripleSet {

	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_DESCRIPTION.resource();
	private final IIdentifiableResource ABSTRACT = EFusionNamespace.DESCRIPTION_HAS_ABSTRACT.resource();
	
	private String description;
	
	public DataDescription(IIRI iri, String description){
		super(iri);
		this.description = description;
	}
	
	public DataDescription(IRDFTripleSet decodedRDFResource) throws IOException {
		//set iri
		super(decodedRDFResource.getSubject().getIdentifier());
		//get object set
		Map<IIdentifiableResource,Set<INode>> objectSet = decodedRDFResource.getObjectSet();
		//set abstract
		INode nAbstract = DataUtilities.getSingleFromObjectSet(objectSet, ABSTRACT, StringLiteral.class, true);
		this.description = ((StringLiteral) nAbstract).getValue();
	}
	
	@Override
	public String getAbstract() {
		return description;
	}

	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(ERDFNamespaces.INSTANCE_OF.resource(), DataUtilities.toSet(TYPE));
		objectSet.put(ABSTRACT, DataUtilities.toSet(new StringLiteral(getAbstract())));
		return objectSet;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}
	
}
