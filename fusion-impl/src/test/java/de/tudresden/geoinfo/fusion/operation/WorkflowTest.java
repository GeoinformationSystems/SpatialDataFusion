package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.BinaryFeatureRelationCollection;
import de.tudresden.geoinfo.fusion.operation.mapping.TopologyRelation;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNModel;
import de.tudresden.geoinfo.fusion.operation.workflow.Workflow;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkflowTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String IN_WITH_INDEX = "IN_WITH_INDEX";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";
    private final static String OUT_RELATIONS = "OUT_RELATIONS";

//    @Test
//    public void executeWorkflow() throws MalformedURLException {
//        executeWorkflow(getWorkflow(), new HashMap<>());
//    }

    @Test
    public void executeBPMNWorkflow() throws MalformedURLException {
        CamundaBPMNModel model = new CamundaBPMNModel(null, "bpmn_test", null);
        model.initModel(getWorkflow());
        System.out.println(model.asXML());
    }

    private IWorkflow getWorkflow() throws MalformedURLException {
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
        //data objects
        Map<String, LiteralData> literalInputMap = new HashMap<>();
        literalInputMap.put("domain", new URLLiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI().toURL()));
        literalInputMap.put("range", new URLLiteral(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI().toURL()));
        literalInputMap.put("spatial index", new BooleanLiteral(true));
        LiteralInputData literalInput = new LiteralInputData(literalInputMap);
        entities.add(literalInput);
        //set connections
        parser_dom.getInputConnector(IN_RESOURCE).addOutputConnector(literalInput.getOutputConnector("domain"));
        parser_dom.getInputConnector(IN_WITH_INDEX).addOutputConnector(literalInput.getOutputConnector("spatial index"));
        parser_dom.getOutputConnector(OUT_FEATURES).addInputConnector(process_top.getInputConnector(IN_DOMAIN));
        parser_ran.getInputConnector(IN_RESOURCE).addOutputConnector(literalInput.getOutputConnector("range"));
        parser_ran.getInputConnector(IN_WITH_INDEX).addOutputConnector(literalInput.getOutputConnector("spatial index"));
        parser_ran.getOutputConnector(OUT_FEATURES).addInputConnector(process_top.getInputConnector(IN_RANGE));
        //return
        return new Workflow(entities);
    }

    private void executeWorkflow(Workflow workflow, HashMap<IIdentifier, IData> input) {

        IIdentifier ID_OUT_RELATIONS = workflow.getOutputConnector(OUT_RELATIONS).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = workflow.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> output = workflow.execute(input);

        Assert.assertNotNull(output);
        Assert.assertFalse(output.isEmpty());
        Assert.assertTrue(output.containsKey(ID_OUT_RELATIONS));
        Assert.assertTrue(output.get(ID_OUT_RELATIONS) instanceof BinaryFeatureRelationCollection);

        BinaryFeatureRelationCollection relations = (BinaryFeatureRelationCollection) output.get(ID_OUT_RELATIONS);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + workflow.getIdentifier() + "\n\t" +
                "number of relations: " + relations.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
