package de.tudresden.gis.fusion.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.ItemAwareElement;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.operation.IOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.provision.TripleStoreGenerator;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;

public class BPMNServiceTaskProxy {
	
	private final String CAMUNDA_SERVICETYPE = "serviceType";
	
	private final String ST_LITERAL = "Literal";
	private final String ST_WFS = "WFS";
	private final String ST_WPS = "WPS";
	private final String ST_SPARQL = "SPARQLEndpoint";
	
	private final String ID_SEPARATOR = "_id-id_";
	private final String NODE_SEPARATOR = "_id-node_";

	private String identifier;
	private Set<String> incoming;
	private Set<String> outgoing;
	private Map<String,String> inputAssociations;
	private Map<String,String> outputAssociations;
	private Map<String,String> camundaProperties = new HashMap<String,String>();
	
	private IOperation operation;
	
	public BPMNServiceTaskProxy(ServiceTask serviceTask, Set<IOperation> availableOperations){
		//set identifier
		this.setIdentifier(serviceTask.getId());
		//set Camunda properties
		CamundaProperties properties = (CamundaProperties) serviceTask.getExtensionElements().getUniqueChildElementByType(CamundaProperties.class);
		if(properties != null)
			this.setCamundaProperties(properties.getChildElementsByType(CamundaProperty.class));
		//set incomings
		this.setIncoming(serviceTask.getIncoming());
		//set outgoings
		this.setOutgoing(serviceTask.getOutgoing());
		//set input associations
		this.setInputAssociations(serviceTask.getDataInputAssociations());
		//set output associations
		this.setOutputAssociations(serviceTask.getDataOutputAssociations());
		//set opreation
		this.setOperation(availableOperations);
	}

	private void setOperation(Set<IOperation> availableOperations) {
		//set null for literals
		if(this.getCamundaServiceType().equalsIgnoreCase(ST_LITERAL))
			operation = null;
		//set GML parser for WFS
		else if(this.getCamundaServiceType().equalsIgnoreCase(ST_WFS))
			operation = new GMLParser();
		//set IOperation by identifier for WPS
		else if(this.getCamundaServiceType().equalsIgnoreCase(ST_WPS))
			operation = getWPSOperation(availableOperations, this.getCamundaProperty("identifier"));
		//set Fuseki provider for SPARQL
		else if(this.getCamundaServiceType().equalsIgnoreCase(ST_SPARQL))
			operation = new TripleStoreGenerator();
		
		else
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "Could not determine service type for BPMN task");
	}

	private IOperation getWPSOperation(Set<IOperation> availableOperations, String key) {
		if(key == null)
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "WPS Process identifier is not set");
		//select process by identifier
		for(IOperation operation : availableOperations){
			//select process, if key contains the simple name of the process
			if(key.contains(operation.getProfile().getProcessName()))
				return operation;
		}
		//else
		throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "Suitable process for identifier + " + key + " cannot be found");
	}

	public String getIdentifier() { return identifier; }
	private void setIdentifier(String identifier) { this.identifier = identifier; }
	
	private void setCamundaProperties(Collection<CamundaProperty> properties) {
		for(CamundaProperty property : properties){
			camundaProperties.put(property.getCamundaName(), property.getCamundaValue());
		}
	}

	public String getCamundaServiceType() { return camundaProperties.get(CAMUNDA_SERVICETYPE); }
	public String getCamundaProperty(String key) { return camundaProperties.get(key); }

	public Set<String> getIncoming() { return incoming; }
	private void setIncoming(Collection<SequenceFlow> incomingFlows) {
		this.incoming = new HashSet<String>();
		for(SequenceFlow incomingFlow : incomingFlows){
			this.incoming.add(split(incomingFlow.getId(), ID_SEPARATOR, 0));
		}
	}

	public Set<String> getOutgoing() { return outgoing; }
	private void setOutgoing(Collection<SequenceFlow> outgoingFlows) {
		this.outgoing = new HashSet<String>();
		for(SequenceFlow outgoingFlow : outgoingFlows){
			this.outgoing.add(split(outgoingFlow.getId(), ID_SEPARATOR, 1));
		}
	}

	public Map<String,String> getInputAssociations() { return inputAssociations; }
	public void setInputAssociations(Collection<DataInputAssociation> dataInputAssociations) {
		this.inputAssociations = new HashMap<String,String>();
		for(DataInputAssociation inputAssociation : dataInputAssociations){
			//set value (member target)
			String memberId = split(inputAssociation.getTarget().getId(), NODE_SEPARATOR, 1);
			//set key (external association id)
			for(ItemAwareElement externalKey : inputAssociation.getSources()){
				inputAssociations.put(externalKey.getId(), memberId);
			}			
		}
	}
	public boolean hasInputAssociation(String key){
		return inputAssociations.containsKey(key);
	}

	public Map<String,String> getOutputAssociations() { return outputAssociations; }
	public void setOutputAssociations(Collection<DataOutputAssociation> dataOutputAssociations) {
		this.outputAssociations = new HashMap<String,String>();
		for(DataOutputAssociation outputAssociation : dataOutputAssociations){
			//set key (external association id)
			String externalKey = outputAssociation.getTarget().getId();
			//set value (member source)
			for(ItemAwareElement memberId : outputAssociation.getSources()){
				outputAssociations.put(externalKey, split(memberId.getId(), NODE_SEPARATOR, 1));
			}
		}
	}
	public boolean hasOutptAssociation(String key){
		return outputAssociations.containsKey(key);
	}
	
	public Collection<String> getRequiredInputKeys(){
		return this.getInputAssociations().values();
	}
	
	public Collection<String> getRequiredOutputKeys(){
		return this.getOutputAssociations().values();
	}
	
	private String split(String string, String separator, int item){
		String[] tmp = string.split(separator);
		if(tmp.length <= item)
			throw new IllegalArgumentException("invalid split configuration: " + string + ":" + separator + ":" + item);
		else
			return tmp[item];
	}
	
	private Map<String,IData> inputs = new HashMap<String,IData>();
	private Map<String,IData> outputs = new HashMap<String,IData>();
	private boolean isExecuted = false;
	public boolean isExecuted() { return isExecuted; }
	
	public void setInputForAssociationId(String associationId, IData value){
		this.setInput(this.getInputAssociations().get(associationId), value);
	}
	
	private void setInput(String key, IData value){
		inputs.put(key, value);
	}
	
	public void setOutput(String key, IData value){
		outputs.put(key, value);
	}
	public void setOutputs(Map<String,IData> outputs){
		this.outputs = outputs;
	}
	
	public Map<String, IData> getOutputs(){
		if(!isExecuted)
			execute();
		return outputs;
	}
	
	public IData getOutput(String key){
		if(!isExecuted)
			execute();
		return outputs.get(key);
	}
	
	public IData getOutputForAssociationId(String associationId){
		return this.getOutput(this.getOutputAssociations().get(associationId));
	}
	
	private void execute() {
		if(!isExecutable())
			return;
		if(this.getCamundaServiceType().equalsIgnoreCase(ST_LITERAL))
			executeLiteral();
		else if(this.getCamundaServiceType().equalsIgnoreCase(ST_WFS))
			executeWFS();
		else if(this.getCamundaServiceType().equalsIgnoreCase(ST_WPS))
			executeWPS();
		else if(this.getCamundaServiceType().equalsIgnoreCase(ST_SPARQL))
			executeSPARQL();
		
		validateResult();
	}

	private void validateResult() {
		if(!this.outputs.keySet().containsAll(getRequiredOutputKeys()))
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "operation " + this.getIdentifier() + " did not produce all of the required outputs");
		this.isExecuted = true;
	}

	private void executeLiteral() {
		String value = this.getCamundaProperty("value");
		if(value == null)
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "Camunda properties contain no literal value");
		this.setOutput("Literal", DataUtilities.encodeLiteral(value));
	}

	private void executeWFS() {
		//get properties
		String base = this.getCamundaProperty("base");
		String typename = this.getCamundaProperty("typename");
		String srsname = this.getCamundaProperty("srsname");
		String bbox = this.getCamundaProperty("bbox");
		URILiteral wfsResource = new URILiteral(getWFSRequest(base, typename, srsname, bbox));
		//set WFS input
		inputs.put("IN_RESOURCE", wfsResource);
		//execute retrieval operation
		this.setOutputs(operation.execute(inputs));
	}

	private String getWFSRequest(String base, String typename, String srsname, String bbox) {
		return base + "?service=WFS&version=1.1.0&request=GetFeature" + 
				"&typename=" + typename + 
				"&srsname=" + srsname +
				(bbox == null || bbox.isEmpty() ? "" : bbox);
	}

	private void executeWPS() {
		this.setOutputs(operation.execute(inputs));
	}

	private void executeSPARQL() {
		//get properties
		String url = this.getCamundaProperty("url");
		//set triple store input properties
		TripleStoreGenerator generator = new TripleStoreGenerator();
		this.setInput("IN_TRIPLE_STORE", new URILiteral(url));
		this.setInput("IN_CLEAR_STORE", new BooleanLiteral(true));
		this.setInput("IN_URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/process#;process;"));
//				+ "http://tu-dresden.de/uw/geo/gis/fusion/measurement/;measurement;"));
		this.setOutputs(generator.execute(inputs));
	}

	public boolean isExecutable() {
		if(!this.inputs.keySet().containsAll(getRequiredInputKeys()))
			return false;
		return true;
	}
	
}
