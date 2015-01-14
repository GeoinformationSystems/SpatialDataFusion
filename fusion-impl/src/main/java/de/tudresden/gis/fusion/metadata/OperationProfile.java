package de.tudresden.gis.fusion.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
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
	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = new LinkedHashMap<IIdentifiableResource,INode>();
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_TITLE.asString()), new StringLiteral(this.getProcessName()));
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DESCRIPTION.asString()), new StringLiteral(this.getProcessDescription()));
		for(IIODescription input : inputs){
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_INPUT.asString()), input);
		}
		for(IIODescription output : outputs){
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_OUTPUT.asString()), output);
		}
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
