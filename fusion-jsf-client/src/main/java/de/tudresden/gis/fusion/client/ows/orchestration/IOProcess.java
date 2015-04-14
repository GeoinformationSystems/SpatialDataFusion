package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.tudresden.gis.fusion.client.ows.orchestration.IONode.NodeType;

public class IOProcess implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String serviceIdentifier;
	private String localIdentifier;
	private Set<IONode> nodes;
	
	public IOProcess(String serviceIdentifier, String localIdentifier, Set<IONode> nodes){
		this.setServiceIdentifier(serviceIdentifier);
		this.setLocalIdentifier(localIdentifier);
		for(IONode node : nodes){
			this.addNode(node);
		};
	}
	
	public IOProcess(String serviceIdentifier, String localIdentifier, IONode node){
		this(serviceIdentifier, localIdentifier, new HashSet<IONode>());
		this.addNode(node);
	}
	
	public String getServiceIdentifier() { return serviceIdentifier; }
	private void setServiceIdentifier(String serviceIdentifier) { this.serviceIdentifier = serviceIdentifier; }
	
	public String getLocalIdentifier() { return localIdentifier; }
	private void setLocalIdentifier(String localIdentifier) { this.localIdentifier = localIdentifier; }
	
	public Set<IONode> getNodes() { return nodes; }
	public void addNode(IONode node){
		if(nodes == null)
			nodes = new HashSet<IONode>();
		nodes.add(node);
		node.setProcess(this);
	}
	
	/**
	 * get node with specified name
	 * @param identifier name
	 * @return node
	 */
	public IONode getNode(String identifier){
		for(IONode node : nodes){
			if(node.getIdentifier().equals(identifier))
				return node;
		}
		return null;
	}
	
	/**
	 * get input nodes of the process
	 * @return input nodes
	 */
	public Set<IONode> getInputNodes() {
		Set<IONode> inputs = new HashSet<IONode>();
		for(IONode node : nodes){
			if(node.getType().equals(NodeType.INPUT) || node.getType().equals(NodeType.BOTH))
				inputs.add(node);
		}
		return inputs;
	}
	
	/**
	 * get output nodes of the process
	 * @return output nodes
	 */
	public Set<IONode> getOutputNodes() {
		Set<IONode> inputs = new HashSet<IONode>();
		for(IONode node : nodes){
			if(node.getType().equals(NodeType.OUTPUT) || node.getType().equals(NodeType.BOTH))
				inputs.add(node);
		}
		return inputs;
	}

	private Set<IOProcess> ancestors = new HashSet<IOProcess>();
	public void addAncestor(IOProcess ancestor) { this.ancestors.add(ancestor); }
	public Set<IOProcess> getAncestors(boolean recursive) { 
		if(!recursive)
			return ancestors;
		else
			return getAncestors(new HashSet<IOProcess>(), false);
	}
	
	/**
	 * get all ancestors recursively
	 * @param visited visited ancestors
	 * @return all ancestors for this process
	 */
	private Set<IOProcess> getAncestors(Set<IOProcess> visited, boolean addThis){
		if(!visited.contains(this)){
			if(addThis)
				visited.add(this);
			for(IOProcess ancestor : this.getAncestors(false)){
				visited.addAll(ancestor.getAncestors(visited, true));
			}
		}
		return visited;
	}
	
	private Set<IOProcess> successors = new HashSet<IOProcess>();
	public void addSuccessor(IOProcess successor) { this.successors.add(successor);	}
	public Set<IOProcess> getSuccessors(boolean recursive) { 
		if(!recursive)
			return successors;
		else
			return getSuccessors(new HashSet<IOProcess>(), false);
	}
	
	/**
	 * get all successors recursively
	 * @param visited visited successors
	 * @return all successors for this process
	 */
	private Set<IOProcess> getSuccessors(HashSet<IOProcess> visited, boolean addThis) {
		if(!visited.contains(this)){
			if(addThis)
				visited.add(this);
			for(IOProcess ancestor : this.getSuccessors(false)){
				visited.addAll(ancestor.getSuccessors(visited, true));
			}
		}
		return visited;
	}
	
	/**
	 * check if process has ancestor
	 * @param process ancestor process to check for
	 * @return true, if process is ancestor of this
	 */
	public boolean hasAncestor(IOProcess process) {
		return this.hasAncestor(process, new HashSet<IOProcess>());
	}
	
	/**
	 * check if process has successor
	 * @param process successor process to check for
	 * @return true, if process is successor of this
	 */
	public boolean hasSuccessor(IOProcess process) {
		return this.hasSuccessor(process, new HashSet<IOProcess>());
	}

	/**
	 * check if process has ancestor
	 * @param process ancestor process to check for
	 * @param visited includes already visited processes
	 * @return true, if process is ancestor of this
	 */
	private boolean hasAncestor(IOProcess process, Set<IOProcess> visited) {
		//return false, if process has already been visited
		if(visited.contains(this))
			return false;
		//add this process to visited processes
		visited.add(this);
		//recursively check for ancestors
		for(IOProcess ancestor : this.getAncestors(false)){
			if(ancestor.equals(process) || ancestor.hasAncestor(process, visited))
				return true;
		}
		return false;
	}
	
	/**
	 * check if process has successor
	 * @param process successor process to check for
	 * @param visited includes already visited processes
	 * @return true, if process is successor of this
	 */
	private boolean hasSuccessor(IOProcess process, Set<IOProcess> visited){
		//return false, if process has already been visited
		if(visited.contains(this))
			return false;
		//add this process to visited processes
		visited.add(this);
		//recursively check for successor
		for(IOProcess successor : this.getSuccessors(false)){
			if(successor.equals(process) || successor.hasSuccessor(process, visited))
				return true;
		}
		return false;
	}
	
	/**
	 * check, if process participates in connection
	 * @param connection input connection
	 * @return true, if process participates in connection
	 */
	public boolean participatesIn(IOConnection connection){
		return this.equals(connection.getStartProcess()) || this.equals(connection.getEndProcess());
	}
	
}
