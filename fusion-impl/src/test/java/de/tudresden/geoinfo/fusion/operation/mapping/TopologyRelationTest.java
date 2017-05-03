package de.tudresden.geoinfo.fusion.operation.mapping;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.BinaryFeatureRelationCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class TopologyRelationTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_EXPLICIT_DISJOINT = "IN_EXPLICIT_DISJOINT";

    private final static String OUT_RELATIONS = "OUT_RELATIONS";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void getTopologyRelations() throws MalformedURLException {
        getTopologyRelations(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI().toURL(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI().toURL(), true),
                null);
    }

    @Test
    public void getTopologyRelationsWithDisjoint() throws MalformedURLException {
        getTopologyRelations(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI().toURL(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI().toURL(), true),
                new BooleanLiteral(true));
    }

    private void getTopologyRelations(GTFeatureCollection domain, GTFeatureCollection range, BooleanLiteral explicitDisjoint) {

        TopologyRelation process = new TopologyRelation();
        IIdentifier ID_IN_DOMAIN = process.getInputConnector(IN_DOMAIN).getIdentifier();
        IIdentifier ID_IN_RANGE = process.getInputConnector(IN_RANGE).getIdentifier();
        IIdentifier ID_IN_EXPLICIT_DISJOINT = process.getInputConnector(IN_EXPLICIT_DISJOINT).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = process.getOutputConnector(OUT_RUNTIME).getIdentifier();
        IIdentifier ID_OUT_RELATIONS = process.getOutputConnector(OUT_RELATIONS).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_DOMAIN, domain);
        input.put(ID_IN_RANGE, range);
        if (explicitDisjoint != null)
            input.put(ID_IN_EXPLICIT_DISJOINT, explicitDisjoint);

        Map<IIdentifier, IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_RELATIONS));
        Assert.assertTrue(output.get(ID_OUT_RELATIONS) instanceof BinaryFeatureRelationCollection);

        BinaryFeatureRelationCollection relations = (BinaryFeatureRelationCollection) output.get(ID_OUT_RELATIONS);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of reference features: " + domain.size() + "\n\t" +
                "number of target features: " + range.size() + "\n\t" +
                "number of relations: " + relations.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
