//package de.tudresden.geoinfo.fusion.operation;
//
//import de.tud.fusion.Utilities;
//import de.tudresden.geoinfo.fusion.data.IData;
//import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
//import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
//import de.tudresden.geoinfo.fusion.data.relation.BinaryFeatureRelationCollection;
//import de.tudresden.geoinfo.fusion.operation.workflow.Workflow;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class CamundaBPMNTest extends AbstractTest {
//
//    private final static String IN_DOMAIN = "IN_DOMAIN";
//    private final static String IN_RANGE = "IN_RANGE";
//    private final static String IN_RESOURCE = "IN_RESOURCE";
//    private final static String IN_WITH_INDEX = "IN_WITH_INDEX";
//
//    private final static String OUT_FEATURES = "OUT_FEATURES";
//    private final static String OUT_RUNTIME = "OUT_RUNTIME";
//    private final static String OUT_RELATIONS = "OUT_RELATIONS";
//
//    @Test
//    public void executeWorkflow() {
//
//    }
//
//    private void executeWorkflow(Set<IWorkflowNode> entities, HashMap<IIdentifier,IData> input){
//
//        Workflow workflow = new Workflow(entities);
//
//        IIdentifier ID_OUT_RELATIONS = workflow.getOutputConnector(OUT_RELATIONS).getIdentifier();
//        IIdentifier ID_OUT_RUNTIME = workflow.getOutputConnector(OUT_RUNTIME).getIdentifier();
//
//        Map<IIdentifier,IData> output = workflow.execute(input);
//
//        Assert.assertNotNull(output);
//        Assert.assertFalse(output.isEmpty());
//        Assert.assertTrue(output.containsKey(ID_OUT_RELATIONS));
//        Assert.assertTrue(output.get(ID_OUT_RELATIONS) instanceof BinaryFeatureRelationCollection);
//
//        BinaryFeatureRelationCollection relations = (BinaryFeatureRelationCollection) output.get(ID_OUT_RELATIONS);
//
//        Runtime runtime = Runtime.getRuntime();
//        runtime.gc();
//        System.out.print("TEST: " + workflow.getIdentifier() + "\n\t" +
//                "number of relations: " + relations.resolve().size() + "\n\t" +
//                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
//                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
//    }
//
//}
