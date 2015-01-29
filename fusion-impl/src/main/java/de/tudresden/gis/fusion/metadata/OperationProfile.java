package de.tudresden.gis.fusion.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;
import de.tudresden.gis.fusion.operation.metadata.IOperationProfile;

public class OperationProfile extends Resource implements IOperationProfile {

	private String name, description;
	private Collection<IIODescription> inputs, outputs;
	
	public OperationProfile(IIRI uri, String name, String description, Collection<IIODescription> inputs, Collection<IIODescription> outputs) {
		super(uri);
		this.name = name;
		this.description = description;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(EFusionNamespace.HAS_TITLE.resource(), DataUtilities.toSet(new StringLiteral(this.getProcessName())));
		objectSet.put(EFusionNamespace.HAS_DESCRIPTION.resource(), DataUtilities.toSet(new StringLiteral(this.getProcessDescription())));
		objectSet.put(EFusionNamespace.HAS_INPUT.resource(), DataUtilities.collectionToSet(inputs));
		objectSet.put(EFusionNamespace.HAS_OUTPUT.resource(), DataUtilities.collectionToSet(outputs));
		return objectSet;
	}

	@Override
	public Collection<IIODescription> getInputDescriptions() {
		return inputs;
	}

	@Override
	public Collection<IIODescription> getOutputDescriptions() {
		return outputs;
	}
	
	public Collection<IIRI> getInputKeys() {
		return getIOKeys(getInputDescriptions());
	}

	public Collection<IIRI> getOutputKeys() {
		return getIOKeys(getOutputDescriptions());
	}

	public IIODescription getInputDescription(IIRI key) {
		return getIOForKey(inputs, key);
	}

	public IIODescription getOutputDescription(IIRI key) {
		return getIOForKey(outputs, key);
	}
	
	private Collection<IIRI> getIOKeys(Collection<IIODescription> ios){
		Collection<IIRI> keys = new ArrayList<IIRI>();
		for(IIODescription io : ios){
			keys.add(io.getIdentifier());
		}
		return keys;
	}
	
	private IIODescription getIOForKey(Collection<IIODescription> ios, IIRI key){
		for(IIODescription io : ios){
			if(io.getIdentifier().equals(key))
				return io;
		}
		return null;
	}

	@Override
	public Collection<IIdentifiableResource> getClassification() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getSubject() {
		return this;
	}

	@Override
	public String getProcessName() {
		return name;
	}

	@Override
	public String getProcessDescription() {
		return description;
	}

}
