package de.tudresden.gis.fusion.metadata;

import java.util.LinkedHashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.metadata.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;

public class DataDescription implements IDataDescription {

	private String description;
	
	public DataDescription(String description){
		this.description = description;
	}
	
	@Override
	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = new LinkedHashMap<IIdentifiableResource,INode>();
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DESCRIPTION.asString()), new StringLiteral(getAbstract()));
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
