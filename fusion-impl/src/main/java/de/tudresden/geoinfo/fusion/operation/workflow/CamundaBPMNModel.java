package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IWorkflowNode;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.xml.ModelValidationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Camunda BPNM workflow description
 */
public class CamundaBPMNModel extends Resource {

    private final String MODEL_NAMESPACE = "http://tu-dresden.de/uw/geo/gis/fusion";

    private BpmnModelInstance model;

    /**
     * constructor
     */
    public CamundaBPMNModel() {
        super(new Identifier());
        this.model = createEmptyModel(this.getIdentifier());
        //TODO model creation
    }

    /**
     * create empty model with definitions
     *
     * @param identifier identifier for bpmn definitions
     * @return model instance
     */
    private BpmnModelInstance createEmptyModel(IIdentifier identifier) {
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace(MODEL_NAMESPACE);
        definitions.setId(identifier.toString());
        modelInstance.setDefinitions(definitions);
        return modelInstance;
    }

    /**
     * create BPMN process
     *
     * @param identifier process identifier
     * @return BPMN process definition
     */
    private Process addBPMNProcess(IIdentifier identifier) {
        //create BPMN process
        Process bpmnProcess = this.model.newInstance(Process.class);
        bpmnProcess.setId(identifier.toString());
        this.model.getDefinitions().addChildElement(bpmnProcess);
        return bpmnProcess;
    }

    /**
     * add BPMN task
     *
     * @param node workflow node
     * @return BPMN task
     */
    private ServiceTask createBPMNTask(Process bpmnProcess, IWorkflowNode node) {

        //create BPMN process
        ServiceTask bpmnTask = this.model.newInstance(ServiceTask.class);
        //TODO
        return bpmnTask;
    }

    /**
     * get XML input stream following BPMN 2.0 schema
     *
     * @return XML input stream
     * @throws ModelValidationException
     */
    public InputStream asXML() throws ModelValidationException {
        return new ByteArrayInputStream(Bpmn.convertToString(this.model).getBytes(StandardCharsets.UTF_8));
    }

}
