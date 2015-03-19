package de.tudresden.gis.fusion.metadata.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class IODescription implements IIODescription,IRDFTripleSet {
	
	private String identifier;
	private String description;
	private IData defaultIO;
	private Collection<IIORestriction> restrictions;
	
	public IODescription(String identifier, String description, IData defaultIO, Collection<IIORestriction> restrictions){
		this.identifier = identifier;
		this.description = description;
		this.defaultIO = defaultIO;
		this.restrictions = restrictions;
	}
	
	public IODescription(String identifier, String description, Collection<IIORestriction> restrictions){
		this(identifier, description, null, restrictions);
	}
	
	public IODescription(String identifier, String description, IData defaultIO, IIORestriction[] restrictions){
		this(identifier, description, defaultIO, Arrays.asList(restrictions));
	}
	
	public IODescription(String identifier, String description, IIORestriction[] restrictions){
		this(identifier, description, null, restrictions);
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public String getAbstract() {
		return description;
	}

	@Override
	public IResource getSubject() {
		return Resource.newEmptyResource();
	}
	
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(EFusionNamespace.HAS_IDENTIFIER.resource(), DataUtilities.toSet(new IdentifiableResource(this.getIdentifier())));
		objectSet.put(EFusionNamespace.HAS_DESCRIPTION.resource(), DataUtilities.toSet(new StringLiteral(this.getAbstract())));
		objectSet.put(EFusionNamespace.HAS_RESTRICTION.resource(), DataUtilities.descriptionsToNodeSet(restrictions));
		objectSet.put(EFusionNamespace.HAS_DEFAULT.resource(), DataUtilities.toSet(((ISimpleData) getDefault()).getRDFRepresentation()));
		return objectSet;
	}

	@Override
	public IData getDefault() {
		return defaultIO;
	}
	
	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

	@Override
	public Collection<IIORestriction> getDataRestrictions() {
		return restrictions;
	}

}
