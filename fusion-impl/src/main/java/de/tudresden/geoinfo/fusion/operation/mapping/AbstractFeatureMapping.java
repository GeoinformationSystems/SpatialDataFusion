package de.tudresden.geoinfo.fusion.operation.mapping;

import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 *
 */
public abstract class AbstractFeatureMapping extends AbstractOperation {

    private final static String IN_DOMAIN_TITLE = "IN_DOMAIN";
    private final static String IN_DOMAIN_DESCRIPTION = "Domain features";
    private final static String IN_RANGE_TITLE = "IN_RANGE";
    private final static String IN_RANGE_DESCRIPTION = "Range features";
    private final static String IN_RELATIONS_TITLE = "IN_RELATIONS";
    private final static String IN_RELATIONS_DESCRIPTION = "Existing relations";
    private final static String IN_MEASUREMENTS_TITLE = "IN_MEASUREMENTS";
    private final static String IN_MEASUREMENTS_DESCRIPTION = "Relation measurements";
    private final static String IN_DROP_RELATIONS_TITLE = "IN_DROP_RELATIONS";
    private final static String IN_DROP_RELATIONS_DESCRIPTION = "Flag: existing relationships can be dropped";

    private final static String OUT_RELATIONS_TITLE = "OUT_RELATIONS";
    private final static String OUT_RELATIONS_DESCRIPTION = "Feature relations with attached measurements";

    /**
     * constructor
     *
     */
    public AbstractFeatureMapping(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void executeOperation() {
        //get input connectors
        IInputConnector sourceConnector = getInputConnector(IN_DOMAIN_TITLE);
        IInputConnector targetConnector = getInputConnector(IN_RANGE_TITLE);
        IInputConnector measurementsConnector = getInputConnector(IN_MEASUREMENTS_TITLE);
        IInputConnector relationsConnector = getInputConnector(IN_RELATIONS_TITLE);
        IInputConnector dropRelationConnector = getInputConnector(IN_DROP_RELATIONS_TITLE);
        //get inputs
        GTFeatureCollection domainFeatures = (GTFeatureCollection) sourceConnector.getData();
        GTFeatureCollection rangeFeatures = (GTFeatureCollection) targetConnector.getData();
        RelationMeasurementCollection measurements = measurementsConnector.getData() != null ? (RelationMeasurementCollection) relationsConnector.getData() : null;
        BinaryFeatureRelationCollection relations = relationsConnector.getData() != null ? (BinaryFeatureRelationCollection) relationsConnector.getData() : null;
        BooleanLiteral dropRelations = (BooleanLiteral) dropRelationConnector.getData();
        //relations based on measurements
        if (relations == null)
            connectOutput(OUT_RELATIONS_TITLE, performRelationMapping(domainFeatures, rangeFeatures, measurements));
            //relations based on measurements attached to existing relations
        else
            connectOutput(OUT_RELATIONS_TITLE, performRelationMapping(domainFeatures, rangeFeatures, relations, dropRelations.resolve()));
    }

    /**
     * perform relation mapping
     *
     * @param domainFeatures domain features
     * @param rangeFeatures  range features
     * @param measurements   relation measurements
     * @return feature relations
     */
    private BinaryFeatureRelationCollection performRelationMapping(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures, RelationMeasurementCollection measurements) {
        BinaryFeatureRelationCollection relations = new BinaryFeatureRelationCollection(null, null, null);
        for (GTVectorFeature domain : domainFeatures) {
            for (GTVectorFeature range : rangeFeatures) {
                Set<IRelationMeasurement> associatedMeasurements = getAssociatedMeasurements(domain, range, measurements);
                BinaryFeatureRelation relation = performRelationMapping(domain, range, associatedMeasurements);
                if (relation != null)
                    relations.add(relation);
            }
        }
        return relations;
    }

    /**
     * get measurements associated with both domain and range feature
     *
     * @param domain       domain feature
     * @param range        range feature
     * @param measurements relation measurement collection
     * @return set of associated measurements
     */
    private Set<IRelationMeasurement> getAssociatedMeasurements(GTVectorFeature domain, GTVectorFeature range, RelationMeasurementCollection measurements) {
        if (measurements == null)
            return null;
        Set<IRelationMeasurement> associatedWithDomain = measurements.getMeasurements(domain.getIdentifier());
        Set<IRelationMeasurement> associatedWithRange = measurements.getMeasurements(range.getIdentifier());
        associatedWithDomain.retainAll(associatedWithRange);
        return associatedWithDomain;
    }

    /**
     * perform relation mapping
     *
     * @param domainFeatures source features
     * @param rangeFeatures  target features
     * @param relations      existing relations
     * @param dropRelations  flag: drop relations, if there is no relation created by this operation
     * @return feature relations
     */
    private BinaryFeatureRelationCollection performRelationMapping(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures, BinaryFeatureRelationCollection relations, boolean dropRelations) {
        BinaryFeatureRelationCollection newRelations = new BinaryFeatureRelationCollection(null, null, null);
        for (BinaryFeatureRelation relation : relations) {
            //check for feature identifier in domain and range features
            GTVectorFeature domain = domainFeatures.getFeatureById(((IBinaryRelation) relation).getDomain().getIdentifier());
            GTVectorFeature range = rangeFeatures.getFeatureById(((IBinaryRelation) relation).getRange().getIdentifier());
            if (domain == null || range == null)
                continue;
            //add relation
            BinaryFeatureRelation newRelation = performRelationMapping(domain, range, relation.getMeasurements());
            if (newRelation == null && dropRelations)
                continue;
            newRelations.add(newRelation);
        }
        return newRelations;
    }

    /**
     * perform relation mapping
     *
     * @param domainFeature domain feature
     * @param rangeFeature  range feature
     * @param measurements  relation measurements associated to domain and range
     * @return feature relation or null, if no relation is established
     */
    public abstract BinaryFeatureRelation performRelationMapping(GTVectorFeature domainFeature, GTVectorFeature rangeFeature, Set<IRelationMeasurement> measurements);

    @Override
    public void initializeInputConnectors() {
        addInputConnector(null, IN_DOMAIN_TITLE, IN_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(null, IN_RANGE_TITLE, IN_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(null, IN_MEASUREMENTS_TITLE, IN_MEASUREMENTS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null,
                null);
        addInputConnector(null, IN_RELATIONS_TITLE, IN_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryFeatureRelationCollection.class)},
                null,
                null);
        addInputConnector(null, IN_DROP_RELATIONS_TITLE, IN_DROP_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_RELATIONS_TITLE,
                OUT_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryFeatureRelationCollection.class)
                },
                null);
    }

}
