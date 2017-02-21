package de.tudresden.geoinfo.fusion.operation.mapping;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Relations;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.InputData;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * topology relation
 */
public class TopologyRelation extends AbstractFeatureMapping {

    private static final String PROCESS_TITLE = TopologyRelation.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on topology relation of geometries";

    private static final String IN_EXPLICIT_DISJOINT_TITLE = "IN_EXPLICIT_DISJOINT";
    private static final String IN_EXPLICIT_DISJOINT_DESCRIPTION = "Flag: include disjoint relations (may significantly increase number of relations)";

    private static final IResource ROLE_DOMAIN = Relations.ROLE_DOMAIN.getResource();
    private static final IResource ROLE_RANGE = Relations.ROLE_RANGE.getResource();

    private static final IIdentifier RELATION_EQUALS_IDENTIFIER = Relations.SF_EQUALS.getResource().getIdentifier();
    private static final String RELATION_EQUALS_TITLE = "Equals";
    private static final String RELATION_EQUALS_DESCRIPTION = "The geometries are equal";
    private static final IIdentifier RELATION_DISJOINT_IDENTIFIER = Relations.SF_DISJOINT.getResource().getIdentifier();
    private static final String RELATION_DISJOINT_TITLE = "Disjoint";
    private static final String RELATION_DISJOINT_DESCRIPTION = "The geometries are disjoint";
    private static final IIdentifier RELATION_TOUCHES_IDENTIFIER = Relations.SF_TOUCHES.getResource().getIdentifier();
    private static final String RELATION_TOUCHES_TITLE = "Touches";
    private static final String RELATION_TOUCHES_DESCRIPTION = "The geometries touch each other";
    private static final IIdentifier RELATION_WITHIN_IDENTIFIER = Relations.SF_WITHIN.getResource().getIdentifier();
    private static final String RELATION_WITHIN_TITLE = "Within";
    private static final String RELATION_WITHIN_DESCRIPTION = "The domain geometry is within range geometry";
    private static final IIdentifier RELATION_CONTAINS_IDENTIFIER = Relations.SF_CONTAINS.getResource().getIdentifier();
    private static final String RELATION_CONTAINS_TITLE = "Contains";
    private static final String RELATION_CONTAINS_DESCRIPTION = "The domain geometry contains range geometry";
    private static final IIdentifier RELATION_OVERLAPS_IDENTIFIER = Relations.SF_OVERLAPS.getResource().getIdentifier();
    private static final String RELATION_OVERLAPS_TITLE = "Overlaps";
    private static final String RELATION_OVERLAPS_DESCRIPTION = "The geometries overlap";
    private static final IIdentifier RELATION_CROSSES_IDENTIFIER = Relations.SF_CROSSES.getResource().getIdentifier();
    private static final String RELATION_CROSSES_TITLE = "Crosses";
    private static final String RELATION_CROSSES_DESCRIPTION = "The domain geometry crosses the range geometry";

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
    public void execute() {
        this.bExplicitDisjoint = ((BooleanLiteral) getInputConnector(IN_EXPLICIT_DISJOINT_TITLE).getData()).resolve();
        super.execute();
    }

    @Override
    public BinaryFeatureRelation performRelationMapping(GTVectorFeature domainFeature, GTVectorFeature rangeFeature, Set<IRelationMeasurement> measurements) {
        //get geometries
        Geometry gDomain = (Geometry) domainFeature.getRepresentation().getDefaultGeometry();
        Geometry gRange = (Geometry) rangeFeature.getRepresentation().getDefaultGeometry();
        if (gDomain.isEmpty() || gRange.isEmpty())
            return null;
        //get intersection matrix
        IntersectionMatrix matrix = gDomain.relate(gRange);
        //check for relation: disjoint
        if (matrix.isDisjoint()) {
            if (bExplicitDisjoint)
                return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_DISJOINT_TITLE), null, null);
            else
                return null;
        }
        //check for relation: equals
        if (matrix.isEquals(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_EQUALS_TITLE), null, null);
            //check for relation: covers
        else if (matrix.isCovers())
            return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_CONTAINS_TITLE), null, null);
            //check for relation: covered by
        else if (matrix.isCoveredBy())
            return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_WITHIN_TITLE), null, null);
            //check for relation: overlaps
        else if (matrix.isOverlaps(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_OVERLAPS_TITLE), null, null);
            //check for relation: crosses
        else if (matrix.isCrosses(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_CROSSES_TITLE), null, null);
            //check for relation: touches
        else if (matrix.isTouches(gDomain.getDimension(), gRange.getDimension()))
            return new BinaryFeatureRelation(null, domainFeature, rangeFeature, relationTypes.get(RELATION_TOUCHES_TITLE), null, null);
        return null;
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_EXPLICIT_DISJOINT_TITLE, IN_EXPLICIT_DISJOINT_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new InputData(new BooleanLiteral(false)).getOutputConnector());
    }

    /**
     * initialize topology relation types
     */
    private void initRelationTypes() {
        relationTypes = new HashMap<>();
        relationTypes.put(RELATION_EQUALS_TITLE, getRelationType(RELATION_EQUALS_IDENTIFIER, RELATION_EQUALS_TITLE, RELATION_EQUALS_DESCRIPTION, true, true, true));
        relationTypes.put(RELATION_DISJOINT_TITLE, getRelationType(RELATION_DISJOINT_IDENTIFIER, RELATION_DISJOINT_TITLE, RELATION_DISJOINT_DESCRIPTION, true, false, false));
        relationTypes.put(RELATION_TOUCHES_TITLE, getRelationType(RELATION_TOUCHES_IDENTIFIER, RELATION_TOUCHES_TITLE, RELATION_TOUCHES_DESCRIPTION, true, false, false));
        relationTypes.put(RELATION_WITHIN_TITLE, getRelationType(RELATION_WITHIN_IDENTIFIER, RELATION_WITHIN_TITLE, RELATION_WITHIN_DESCRIPTION, false, true, true));
        relationTypes.put(RELATION_CONTAINS_TITLE, getRelationType(RELATION_CONTAINS_IDENTIFIER, RELATION_CONTAINS_TITLE, RELATION_CONTAINS_DESCRIPTION, false, true, true));
        relationTypes.put(RELATION_OVERLAPS_TITLE, getRelationType(RELATION_OVERLAPS_IDENTIFIER, RELATION_OVERLAPS_TITLE, RELATION_OVERLAPS_DESCRIPTION, true, false, true));
        relationTypes.put(RELATION_CROSSES_TITLE, getRelationType(RELATION_CROSSES_IDENTIFIER, RELATION_CROSSES_TITLE, RELATION_CROSSES_DESCRIPTION, false, true, true));
    }

    /**
     * get topology relation type
     *
     * @param identifier   relation type identifier
     * @param title        relation type title
     * @param description  relation type description
     * @param isSymmetric  flag: relation type is symmetric
     * @param isTransitive flag: relation type is transitive
     * @param isReflexive  flag: relation type is reflexive
     * @return binary relation type
     */
    private IBinaryRelationType getRelationType(IIdentifier identifier, String title, String description, boolean isSymmetric, boolean isTransitive, boolean isReflexive) {
        return new BinaryRelationType(
                identifier, title, description,
                new Role(ROLE_DOMAIN.getIdentifier()),
                new Role(ROLE_RANGE.getIdentifier()),
                isSymmetric,
                isTransitive,
                isReflexive);
    }

}
