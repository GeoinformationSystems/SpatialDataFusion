package de.tudresden.geoinfo.fusion.operation.mapping;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.relation.BinaryRelationCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TopologyRelationTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_EXPLICIT_DISJOINT = "IN_EXPLICIT_DISJOINT";

    private final static String OUT_RELATIONS = "OUT_RELATIONS";

    @Test
    public void getTopologyRelations() throws IOException {
        getTopologyRelations(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                null);
    }

    @Test
    public void getTopologyRelationsWithDisjoint() throws IOException {
        getTopologyRelations(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new BooleanLiteral(true));
    }

    private void getTopologyRelations(GTFeatureCollection domain, GTFeatureCollection range, BooleanLiteral explicitDisjoint) {

        AbstractOperation operation = new TopologyRelation();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_DOMAIN, domain);
        inputs.put(IN_RANGE, range);
        if (explicitDisjoint != null)
            inputs.put(IN_EXPLICIT_DISJOINT, explicitDisjoint);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_RELATIONS, BinaryRelationCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
