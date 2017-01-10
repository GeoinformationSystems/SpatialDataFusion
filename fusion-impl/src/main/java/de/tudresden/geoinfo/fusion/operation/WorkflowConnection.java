package de.tudresden.geoinfo.fusion.operation;

/**
 * Workflow connection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WorkflowConnection implements IWorkflowConnection {

	private IInputConnector input;
	private IOutputConnector output;
	
	/**
	 * constructor
	 * @param input input connector
	 * @param output output connector
	 */
	public WorkflowConnection(IInputConnector input, IOutputConnector output){
		this.input = input;
		this.output = output;
		validate();
	}
	
	@Override
	public IInputConnector getInput() {
		return input;
	}

	@Override
	public IOutputConnector getOutput() {
		return output;
	}
	
	/**
	 * validate connection
	 */
	public void validate() {
		//connect output to input
		getInput().connect(getOutput().getData());
	}

}
