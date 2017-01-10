package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.Map;

/**
 * Workflow engine
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IWorkflowEngine extends IOperation {
	
	/**
	 * execute a workflow
	 * @param input input workflow
	 * @return process outputs
	 */
    Map<IIdentifier,IData> execute(IWorkflow input);

}
