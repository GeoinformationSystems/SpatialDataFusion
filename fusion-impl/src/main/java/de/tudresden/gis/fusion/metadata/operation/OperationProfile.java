package de.tudresden.gis.fusion.metadata.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.operation.IOperationProfile;

public class OperationProfile extends Resource implements IOperationProfile,IRDFTripleSet {

	private String name, description;
	private Set<IIODescription> inputs, outputs;
	private Set<IIdentifiableResource> classification;
	
	public OperationProfile(IIRI uri, Set<IIdentifiableResource> classification, String name, String description, Set<IIODescription> inputs, Set<IIODescription> outputs) {
		super(uri);
		this.name = name;
		this.description = description;
		this.inputs = inputs;
		this.outputs = outputs;
		this.classification = classification;
	}

	@Override
	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = new LinkedHashMap<IIdentifiableResource,Set<INode>>();
		objectSet.put(EFusionNamespace.HAS_TITLE.resource(), DataUtilities.toSet(new StringLiteral(this.getProcessName())));
		objectSet.put(EFusionNamespace.HAS_DESCRIPTION.resource(), DataUtilities.toSet(new StringLiteral(this.getProcessDescription())));
		objectSet.put(EFusionNamespace.HAS_INPUT.resource(), DataUtilities.descriptionsToNodeSet(inputs));
		objectSet.put(EFusionNamespace.HAS_OUTPUT.resource(), DataUtilities.descriptionsToNodeSet(outputs));
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
	
	public Collection<String> getInputKeys() {
		return getIOKeys(getInputDescriptions());
	}

	public Collection<String> getOutputKeys() {
		return getIOKeys(getOutputDescriptions());
	}

	public IIODescription getInputDescription(String key) {
		return getIOForKey(inputs, key);
	}

	public IIODescription getOutputDescription(String key) {
		return getIOForKey(outputs, key);
	}
	
	private Collection<String> getIOKeys(Collection<IIODescription> ios){
		Collection<String> keys = new ArrayList<String>();
		for(IIODescription io : ios){
			keys.add(io.getIdentifier());
		}
		return keys;
	}
	
	private IIODescription getIOForKey(Collection<IIODescription> ios, String key){
		for(IIODescription io : ios){
			if(io.getIdentifier().equals(key))
				return io;
		}
		return null;
	}

	@Override
	public Set<IIdentifiableResource> getClassification() {
		return classification;
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
	
	@Override
	public IRDFRepresentation getRDFRepresentation() {
		return this;
	}

	@Override
	public String getAbstract() {
		return getProcessDescription();
	}

}
