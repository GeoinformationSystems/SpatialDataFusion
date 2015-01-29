package de.tudresden.gis.fusion.metadata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;

public class DataDescription implements IDataDescription {

	private String description;
	
	public DataDescription(String description){
		this.description = description;
	}
	
	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(EFusionNamespace.HAS_DESCRIPTION.resource(), DataUtilities.toSet(new StringLiteral(getAbstract())));
		return objectSet;
	}

	@Override
	public String getAbstract() {
		return description;
	}

	@Override
	public IIRI getIdentifier() {
		return this.getSubject().getIdentifier();
	}

	@Override
	public IResource getSubject() {
		return Resource.newEmptyResource();
	}
	
}
