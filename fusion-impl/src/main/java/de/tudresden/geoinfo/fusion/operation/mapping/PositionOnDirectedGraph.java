//package de.tudresden.geoinfo.fusion.operation.mapping;
//
//import com.vividsolutions.jts.geom.LineString;
//import de.tudresden.geoinfo.fusion.data.IIdentifier;
//import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
//import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
//import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
//import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
//import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
//import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Relations;
//import de.tudresden.geoinfo.fusion.data.relation.*;
//import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
//import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
//import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
//import de.tudresden.geoinfo.fusion.operation.constraint.GeometryBindingConstraint;
//import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * topology relation
// */
//public class PositionOnDirectedGraph extends AbstractOperation {
//
//    private static final String PROCESS_TITLE = PositionOnDirectedGraph.class.getName();
//    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on the relative position of objects on a directed graph";
//
//    private static final IRole ROLE_DOMAIN = new Role(Relations.ROLE_DOMAIN.getResource());
//    private static final IRole ROLE_RANGE = new Role(Relations.ROLE_RANGE.getResource());
//
//    private static final String IN_FEATURES_TITLE = "IN_FEATURES";
//    private static final String IN_FEATURES_DESCRIPTION = "Input features that are related based on their position on a directed graph";
//    private static final String IN_GRAPH_TITLE = "IN_GRAPH";
//    private static final String IN_GRAPH_DESCRIPTION = "Directed input graph";
//    private static final String IN_TOLERANCE_TITLE = "IN_TOLERANCE";
//    private static final String IN_TOLERANCE_DESCRIPTION = "Input tolerance for the assignment of features to the graph";
//    private static final String IN_EXPLICIT_DISJOINT_TITLE = "IN_EXPLICIT_DISJOINT";
//    private static final String IN_EXPLICIT_DISJOINT_DESCRIPTION = "Flag: include disjoint relations (may significantly increase number of relations)";
//
//    private final static String OUT_RELATIONS_TITLE = "OUT_RELATIONS";
//    private final static String OUT_RELATIONS_DESCRIPTION = "Feature relations based on the connection of features on the directed graph";
//
//    private static final IRDFProperty RELATION_EQUALS_IDENTIFIER = Relations.GRAPH_EQUALS.getResource();
//    private static final String RELATION_EQUALS_TITLE = "Equals";
//    private static final IRDFProperty RELATION_DOWNWARDS_IDENTIFIER = Relations.GRAPH_DOWNWARDS.getResource();
//    private static final String RELATION_DOWNWARDS_TITLE = "Downwards";
//    private static final IRDFProperty RELATION_UPWARDS_IDENTIFIER = Relations.GRAPH_UPWARDS.getResource();
//    private static final String RELATION_UPWARDS_TITLE = "Upwards";
//    private static final IRDFProperty RELATION_DISJOINT_IDENTIFIER = Relations.GRAPH_DISJOINT.getResource();
//    private static final String RELATION_DISJOINT_TITLE = "Disjoint";
//
//    private Map<String, IBinaryRelationType> relationTypes;
//
//    /**
//     * constructor
//     */
//    public PositionOnDirectedGraph() {
//        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
//        initRelationTypes();
//    }
//
//    @Override
//    public void executeOperation() {
//
//        GTFeatureCollection features = (GTFeatureCollection) getInputData(IN_FEATURES_TITLE);
//        GTFeatureCollection graph = (GTFeatureCollection) getInputData(IN_GRAPH_TITLE);
//        double dTolerance = ((DecimalLiteral) getInputData(IN_TOLERANCE_TITLE)).resolve();
//        boolean bExplicitDisjoint = ((BooleanLiteral) getInputData(IN_EXPLICIT_DISJOINT_TITLE)).resolve();
//
//        setOutput(OUT_RELATIONS_TITLE, performRelationMapping(features, graph, dTolerance, bExplicitDisjoint));
//    }
//
//    private BinaryRelationCollection performRelationMapping(GTFeatureCollection features, GTFeatureCollection graph, double tolerance, boolean explicitDisjoint) {
//
//        validateGraph(graph);
//
//        BinaryRelationCollection relations = new BinaryRelationCollection(null, null, null);
//        for (GTVectorFeature domain : features) {
//            for (GTVectorFeature range : features) {
//                if(domain.equals(range))
//                    continue;
//                BinaryRelation relation = performRelationMapping(domain, range, graph);
//                if (relation != null)
//                    relations.add(relation);
//            }
//        }
//
//        return relations;
//
//    }
//
//    @Override
//    public void initializeInputConnectors() {
//        addInputConnector(IN_FEATURES_TITLE, IN_FEATURES_DESCRIPTION,
//                new IRuntimeConstraint[]{
//                        new BindingConstraint(GTFeatureCollection.class),
//                        new MandatoryDataConstraint()},
//                null,
//                null);
//        addInputConnector(IN_GRAPH_TITLE, IN_GRAPH_DESCRIPTION,
//                new IRuntimeConstraint[]{
//                        new BindingConstraint(GTFeatureCollection.class),
//                        new GeometryBindingConstraint(LineString.class),
//                        new MandatoryDataConstraint()},
//                null,
//                null);
//        addInputConnector(IN_TOLERANCE_TITLE, IN_TOLERANCE_DESCRIPTION,
//                new IRuntimeConstraint[]{
//                        new BindingConstraint(DecimalLiteral.class)},
//                null,
//                new DecimalLiteral(0d));
//        addInputConnector(IN_EXPLICIT_DISJOINT_TITLE, IN_EXPLICIT_DISJOINT_DESCRIPTION,
//                new IRuntimeConstraint[]{
//                        new BindingConstraint(BooleanLiteral.class)},
//                null,
//                new BooleanLiteral(false));
//    }
//
//    @Override
//    public void initializeOutputConnectors() {
//        addOutputConnector(OUT_RELATIONS_TITLE,
//                OUT_RELATIONS_DESCRIPTION,
//                new IRuntimeConstraint[]{
//                        new BindingConstraint(BinaryRelationCollection.class)
//                },
//                null);
//    }
//
//    /**
//     * initialize topology relation types
//     */
//    private void initRelationTypes() {
//        relationTypes = new HashMap<>();
//        relationTypes.put(RELATION_EQUALS_TITLE, getRelationType(RELATION_EQUALS_IDENTIFIER, true, true, true));
//        relationTypes.put(RELATION_DOWNWARDS_TITLE, getRelationType(RELATION_DOWNWARDS_IDENTIFIER, false, true, false));
//        relationTypes.put(RELATION_UPWARDS_TITLE, getRelationType(RELATION_UPWARDS_IDENTIFIER, false, true, false));
//        relationTypes.put(RELATION_DISJOINT_TITLE, getRelationType(RELATION_DISJOINT_IDENTIFIER, true, false, false));
//    }
//
//    /**
//     * get topology relation type
//     *
//     * @param identifier   relation type identifier
//     * @param isSymmetric  flag: relation type is symmetric
//     * @param isTransitive flag: relation type is transitive
//     * @param isReflexive  flag: relation type is reflexive
//     * @return binary relation type
//     */
//    private IBinaryRelationType getRelationType(IRDFProperty identifier, boolean isSymmetric, boolean isTransitive, boolean isReflexive) {
//        return new BinaryRelationType(
//                identifier.getIRI(),
//                ROLE_DOMAIN,
//                ROLE_RANGE,
//                isSymmetric,
//                isTransitive,
//                isReflexive);
//    }
//
//}
