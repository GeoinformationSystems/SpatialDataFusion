package de.tudresden.geoinfo.fusion.operation.mapping;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Relations;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * topology relation
 */
public class TopologyRelation extends AbstractFeatureMapping {

    private static final String PROCESS_TITLE = TopologyRelation.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on topology relation of geometries";

    private static final String IN_EXPLICIT_DISJOINT_TITLE = "IN_EXPLICIT_DISJOINT";
    private static final String IN_EXPLICIT_DISJOINT_DESCRIPTION = "Flag: include disjoint relations (may significantly increase number of relations)";

    private static final IRole ROLE_DOMAIN = new Role(Relations.ROLE_DOMAIN.getResource());
    private static final IRole ROLE_RANGE = new Role(Relations.ROLE_RANGE.getResource());

    private static final IRDFProperty RELATION_EQUALS_IDENTIFIER = Relations.SF_EQUALS.getResource();
    private static final String RELATION_EQUALS_TITLE = "Equals";
    private static final IRDFProperty RELATION_DISJOINT_IDENTIFIER = Relations.SF_DISJOINT.getResource();
    private static final String RELATION_DISJOINT_TITLE = "Disjoint";
    private static final IRDFProperty RELATION_TOUCHES_IDENTIFIER = Relations.SF_TOUCHES.getResource();
    private static final String RELATION_TOUCHES_TITLE = "Touches";
    private static final IRDFProperty RELATION_WITHIN_IDENTIFIER = Relations.SF_WITHIN.getResource();
    private static final String RELATION_WITHIN_TITLE = "Within";
    private static final IRDFProperty RELATION_CONTAINS_IDENTIFIER = Relations.SF_CONTAINS.getResource();
    private static final String RELATION_CONTAINS_TITLE = "Contains";
    private static final IRDFProperty RELATION_OVERLAPS_IDENTIFIER = Relations.SF_OVERLAPS.getResource();
    private static final String RELATION_OVERLAPS_TITLE = "Overlaps";
    private static final IRDFProperty RELATION_CROSSES_IDENTIFIER = Relations.SF_CROSSES.getResource();
    private static final String RELATION_CROSSES_TITLE = "Crosses";

    private Map<String, IBinaryRelationType> relationTypes;
    private boolean bExplicitDisjoint;

    /**
     * constructor
     */
    public TopologyRelation() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
        initRelationTypes();
    }

    @Override
    public void executeOperation() {
        //noinspection ConstantConditions
        this.bExplicitDisjoint = ((BooleanLiteral) this.getInputData(IN_EXPLICIT_DISJOINT_TITLE)).resolve();
        super.executeOperation();
    }

    @Override
    public BinaryRelation performRelationMapping(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature, @Nullable Set<IRelationMeasurement> measurements) {

        //get geometries
        Geometry gDomain = Utilities.getGeometry(domainFeature, null, true);
        Geometry gRange = Utilities.getGeometry(rangeFeature, null, true);
        if (gDomain == null || gRange == null)
            return null;

        //get intersection matrix
        IntersectionMatrix matrix = gDomain.relate(gRange);
        //check for relation: disjoint
        if (matrix.isDisjoint()) {
            if (bExplicitDisjoint)
                return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_DISJOINT_TITLE), null, null);
            else
                return null;
        }
        //check for relation: globallyEquals
        if (matrix.isEquals(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_EQUALS_TITLE), null, null);
            //check for relation: covers
        else if (matrix.isCovers())
            return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_CONTAINS_TITLE), null, null);
            //check for relation: covered by
        else if (matrix.isCoveredBy())
            return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_WITHIN_TITLE), null, null);
            //check for relation: overlaps
        else if (matrix.isOverlaps(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_OVERLAPS_TITLE), null, null);
            //check for relation: crosses
        else if (matrix.isCrosses(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_CROSSES_TITLE), null, null);
            //check for relation: touches
        else if (matrix.isTouches(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryRelation(new ResourceIdentifier(), domainFeature, rangeFeature, relationTypes.get(RELATION_TOUCHES_TITLE), null, null);
        return null;
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_EXPLICIT_DISJOINT_TITLE, IN_EXPLICIT_DISJOINT_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    /**
     * initialize topology relation types
     */
    private void initRelationTypes() {
        relationTypes = new HashMap<>();
        relationTypes.put(RELATION_EQUALS_TITLE, getRelationType(RELATION_EQUALS_IDENTIFIER, true, true, true));
        relationTypes.put(RELATION_DISJOINT_TITLE, getRelationType(RELATION_DISJOINT_IDENTIFIER, true, false, false));
        relationTypes.put(RELATION_TOUCHES_TITLE, getRelationType(RELATION_TOUCHES_IDENTIFIER, true, false, false));
        relationTypes.put(RELATION_WITHIN_TITLE, getRelationType(RELATION_WITHIN_IDENTIFIER, false, true, true));
        relationTypes.put(RELATION_CONTAINS_TITLE, getRelationType(RELATION_CONTAINS_IDENTIFIER, false, true, true));
        relationTypes.put(RELATION_OVERLAPS_TITLE, getRelationType(RELATION_OVERLAPS_IDENTIFIER, true, false, true));
        relationTypes.put(RELATION_CROSSES_TITLE, getRelationType(RELATION_CROSSES_IDENTIFIER, false, true, true));
    }

    /**
     * get topology relation type
     *
     * @param identifier   relation type identifier
     * @param isSymmetric  flag: relation type is symmetric
     * @param isTransitive flag: relation type is transitive
     * @param isReflexive  flag: relation type is reflexive
     * @return binary relation type
     */
    private IBinaryRelationType getRelationType(@NotNull IRDFProperty identifier, boolean isSymmetric, boolean isTransitive, boolean isReflexive) {
        return new BinaryRelationType(
                identifier.getIRI(),
                ROLE_DOMAIN,
                ROLE_RANGE,
                isSymmetric,
                isTransitive,
                isReflexive);
    }

}
