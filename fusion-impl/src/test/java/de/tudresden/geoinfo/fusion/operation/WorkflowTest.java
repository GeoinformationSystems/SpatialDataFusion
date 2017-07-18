package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.relation.BinaryRelationCollection;
import de.tudresden.geoinfo.fusion.operation.mapping.TopologyRelation;
import de.tudresden.geoinfo.fusion.operation.ows.WPSProxy;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNModel;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNWorkflow;
import de.tudresden.geoinfo.fusion.operation.workflow.Workflow;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkflowTest extends AbstractTest {

    private final static String WPS_ROOT = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
    private final static String WPS_PROCESS = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";
    private final static String WPS_IN_DATA = "data";
    private final static String WPS_IN_DATA_VALUE = "http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs&MAXFEATURES=1";
    private final static String WPS_IN_WIDTH = "width";
    private final static double WPS_IN_WIDTH_VALUE = 0.1;
    private final static double WPS_IN_WIDTH_VALUE_1 = 1;
    private final static String WPS_OUT_RESULT = "result";

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String IN_WITH_INDEX = "IN_WITH_INDEX";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RELATIONS = "OUT_RELATIONS";

    @Test
    public void executeWorkflow() throws MalformedURLException {
        executeWorkflow(getWorkflow());
    }

    @Test
    public void executeProxyWorkflow() throws MalformedURLException {
        executeProxyWorkflow(getProxyWorkflow());
    }

    @Test
    public void executeBPMNWorkflow() throws MalformedURLException {

        //create BPMN workflow
        Workflow workflow = getWorkflow();
        CamundaBPMNModel model = new CamundaBPMNModel(new ResourceIdentifier(), workflow);
//        System.out.println(model.asXML());

        //re-read and execute workflow
        Workflow bpnmWorkflow = new CamundaBPMNWorkflow(model);
        executeWorkflow(bpnmWorkflow);

    }

    private void executeWorkflow(Workflow workflow) throws MalformedURLException {

            //execute
            Map<String,IData> inputs = new HashMap<>();
            inputs.put(IN_RESOURCE, new URLLiteral(new File("src/test/resources/lines1.shp").toURI().toURL()));
            inputs.put(IN_RESOURCE + "_1", new URLLiteral(new File("src/test/resources/lines2.shp").toURI().toURL()));
            inputs.put(IN_WITH_INDEX, new BooleanLiteral(true));
            inputs.put(IN_WITH_INDEX + "_1", new BooleanLiteral(true));

            Map<String,Class<? extends IData>> outputs = new HashMap<>();
            outputs.put(OUT_RELATIONS, BinaryRelationCollection.class);

            this.execute(workflow, inputs, outputs);

    }

    private void executeProxyWorkflow(Workflow workflow) throws MalformedURLException {

        //execute
        Map<String,IData> inputs = new HashMap<>();
        inputs.put(WPS_IN_DATA, new URLLiteral(WPS_IN_DATA_VALUE, new IOFormat("text/xml", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", null)));
        inputs.put(WPS_IN_WIDTH, new DecimalLiteral(WPS_IN_WIDTH_VALUE));
        inputs.put(WPS_IN_WIDTH + "_1", new DecimalLiteral(WPS_IN_WIDTH_VALUE_1));

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_RELATIONS, BinaryRelationCollection.class);

        this.execute(workflow, inputs, outputs);

    }

    private Workflow getWorkflow() {

        Set<IWorkflowNode> entities = new HashSet<>();
        //1st process
        ShapefileParser parser_dom = new ShapefileParser();
        entities.add(parser_dom);
        //2nd process
        ShapefileParser parser_ran = new ShapefileParser();
        entities.add(parser_ran);
        //3rd process
        TopologyRelation process_top = new TopologyRelation();
        entities.add(process_top);
        //set connections
        parser_dom.getOutputConnector(OUT_FEATURES).connect(process_top.getInputConnector(IN_DOMAIN));
        parser_ran.getOutputConnector(OUT_FEATURES).connect(process_top.getInputConnector(IN_RANGE));
        //init workflow
        Workflow workflow = new Workflow(entities);

        //data input
        Set<IInputConnector> inputConnectors = new HashSet<>();
        inputConnectors.add(parser_dom.getInputConnector(IN_RESOURCE));
        inputConnectors.add(parser_dom.getInputConnector(IN_WITH_INDEX));
        inputConnectors.add(parser_ran.getInputConnector(IN_RESOURCE));
        inputConnectors.add(parser_ran.getInputConnector(IN_WITH_INDEX));
        workflow.initializeInputConnectors(inputConnectors);

        //data output
        Set<IOutputConnector> outputConnectors = new HashSet<>();
        outputConnectors.add(process_top.getOutputConnector(OUT_RELATIONS));
        workflow.initializeOutputConnectors(outputConnectors);

        //return workflow
        return workflow;

    }

    private Workflow getProxyWorkflow() {

        Set<IWorkflowNode> entities = new HashSet<>();

        WPSProxy process1 = new WPSProxy(new URLLiteral(WPS_ROOT));
        process1.setProcessId(WPS_PROCESS);
        entities.add(process1);

        WPSProxy process2 = new WPSProxy(new URLLiteral(WPS_ROOT));
        process2.setProcessId(WPS_PROCESS);
        entities.add(process2);

        //set connections
        process1.getOutputConnector(WPS_OUT_RESULT).connect(process2.getInputConnector(WPS_IN_DATA));

        //init workflow
        Workflow workflow = new Workflow(entities);

        //data input
        Set<IInputConnector> inputConnectors = new HashSet<>();
        inputConnectors.add(process1.getInputConnector(WPS_IN_DATA));
        inputConnectors.add(process1.getInputConnector(WPS_IN_WIDTH));
        inputConnectors.add(process2.getInputConnector(WPS_IN_WIDTH));
        workflow.initializeInputConnectors(inputConnectors);

        //data output
        Set<IOutputConnector> outputConnectors = new HashSet<>();
        outputConnectors.add(process2.getOutputConnector(WPS_OUT_RESULT));
        workflow.initializeOutputConnectors(outputConnectors);

        //return workflow
        return workflow;

    }

}
