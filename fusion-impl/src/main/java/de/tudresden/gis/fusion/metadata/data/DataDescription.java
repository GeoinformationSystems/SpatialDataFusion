package de.tudresden.gis.fusion.metadata.data;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IDescription;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;

public class DataDescription extends Resource implements IDescription,IRDFTripleSet {

	private final IIdentifiableResource TYPE = EFusionNamespace.RDF_TYPE_DESCRIPTION.resource();
	private final IIdentifiableResource ABSTRACT = EFusionNamespace.DESCRIPTION_HAS_ABSTRACT.resource();
	
	private String description;
	
	public DataDescription(IIRI iri, String description){
		super(iri);
		this.description = description;
	}
	
	public DataDescription(INode decodedRDFResource) throws IOException {
		if(decodedRDFResource instanceof IRDFTripleSet){
			//set iri
			super.setIdentifier(((IRDFTripleSet) decodedRDFResource).getSubject().getIdentifier());
			//get object set
			Map<IIdentifiableResource,Set<INode>> objectSet = ((IRDFTripleSet) decodedRDFResource).getObjectSet();
			//set abstract
			INode nAbstract = DataUtilities.getSingleFromObjectSet(objectSet, ABSTRACT, StringLiteral.class, true);
			this.description = ((StringLiteral) nAbstract).getValue();
		}
		else if (decodedRDFResource instanceof IResource){
			super.setIdentifier(((IResource) decodedRDFResource).getIdentifier());
			this.description = null;
		}
		else
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "Description must implement IRDFTripleSet or IResource");
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
