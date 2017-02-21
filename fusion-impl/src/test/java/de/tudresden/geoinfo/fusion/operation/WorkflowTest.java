package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.BinaryFeatureRelationCollection;
import de.tudresden.geoinfo.fusion.operation.mapping.TopologyRelation;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import de.tudresden.geoinfo.fusion.operation.workflow.Workflow;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
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

    @Test
    public void executeWorkflow() {
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
        InputData data_dom = new InputData(new URILiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI()));
        entities.add(data_dom);
        InputData data_ran = new InputData(new URILiteral(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI()));
        entities.add(data_ran);
        InputData data_index = new InputData(new BooleanLiteral(true));
        entities.add(data_index);
        //set connections
        parser_dom.getInputConnector(IN_RESOURCE).addOutputConnector(data_dom.getOutputConnector());
        parser_dom.getInputConnector(IN_WITH_INDEX).addOutputConnector(data_index.getOutputConnector());
        parser_dom.getOutputConnector(OUT_FEATURES).addInputConnector(process_top.getInputConnector(IN_DOMAIN));
        parser_ran.getInputConnector(IN_RESOURCE).addOutputConnector(data_ran.getOutputConnector());
        parser_ran.getInputConnector(IN_WITH_INDEX).addOutputConnector(data_index.getOutputConnector());
        parser_ran.getOutputConnector(OUT_FEATURES).addInputConnector(process_top.getInputConnector(IN_RANGE));
        //execute
        executeWorkflow(entities, new HashMap<>());
    }

    private void executeWorkflow(Set<IWorkflowNode> entities, HashMap<IIdentifier, IData> input) {

        Workflow workflow = new Workflow(entities);

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
