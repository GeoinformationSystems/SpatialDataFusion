package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.tudresden.gis.fusion.client.ows.document.desc.IOFormat;

public class IONode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String identifier;
	private IOFormat defaultFormat;
	private Set<IOFormat> supportedFormats;
	private NodeType type;
	private IOProcess process;
	
	/**
	 * construct process node
	 * @param identifier node identifier
	 * @param defaultFormat default format
	 * @param supportedFormats supported formats
	 */
	public IONode(IOProcess process, String identifier, IOFormat defaultFormat, Set<IOFormat> supportedFormats, NodeType type){
		this.setProcess(process);
		this.setIdentifier(identifier);
		this.setDefaultFormat(defaultFormat);
		this.setSupportedFormats(supportedFormats);
		this.setType(type);
	}
	
	public IONode(IOProcess process, String identifier, IOFormat defaultFormat, NodeType type){
		this(process, identifier, defaultFormat, new HashSet<IOFormat>(), type);
		this.addSupportedFormat(defaultFormat);
	}
	
	public IOProcess getProcess() { return process; }
	public void setProcess(IOProcess process) {	this.process = process;	}
	
	public String getIdentifier() { return identifier; }
	public void setIdentifier(String identifier) { this.identifier = identifier; }
	
	public IOFormat getDefaultFormat() { return defaultFormat; }
	public void setDefaultFormat(IOFormat defaultFormat) { this.defaultFormat = defaultFormat; }

	public Set<IOFormat> getSupportedFormats() { return supportedFormats; }
	public void setSupportedFormats(Set<IOFormat> supportedFormats) { this.supportedFormats = supportedFormats; }
	public void addSupportedFormat(IOFormat format){
		if(supportedFormats == null)
			supportedFormats = new HashSet<IOFormat>();
		supportedFormats.add(format);
	}
	
	public NodeType getType() { return type; }
	public void setType(NodeType type) { this.type = type; }
	
	//incomming connection
	private IOConnection incommingConnection;
	public IOConnection getIncommingConnection() { return incommingConnection; }
	public void setIncommingConnection(IOConnection incommingConnection) { this.incommingConnection = incommingConnection; }
	
	//outgoing connections
	private Set<IOConnection> outgoingConnections = new HashSet<IOConnection>();
	public Set<IOConnection> getOutgoingConnections() { return outgoingConnections; }
	public void setOutgoingConnections(Set<IOConnection> outgoingConnections) { this.outgoingConnections = outgoingConnections; }
	public void addOutgoingConnection(IOConnection outgoingConnection){ outgoingConnections.add(outgoingConnection); }

	/**
	 * defines process node type
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	public enum NodeType {
		INPUT, OUTPUT, BOTH
	}

}
