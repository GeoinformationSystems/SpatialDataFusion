package de.tudresden.geoinfo.fusion.operation.mapping;

import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.relation.BinaryRelation;
import de.tudresden.geoinfo.fusion.data.relation.BinaryRelationCollection;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;
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
    public AbstractFeatureMapping(@NotNull String title, @Nullable String description) {
        super(title, description);
    }

    @Override
    public void executeOperation() {

        //get inputs
        GTFeatureCollection domainFeatures = (GTFeatureCollection) this.getMandatoryInputData(IN_DOMAIN_TITLE);
        GTFeatureCollection rangeFeatures = (GTFeatureCollection) this.getMandatoryInputData(IN_RANGE_TITLE);
        RelationMeasurementCollection measurements = (RelationMeasurementCollection) this.getInputData(IN_MEASUREMENTS_TITLE);
        BinaryRelationCollection relations = (BinaryRelationCollection) this.getInputData(IN_RELATIONS_TITLE);
        BooleanLiteral dropRelations = (BooleanLiteral) this.getMandatoryInputData(IN_DROP_RELATIONS_TITLE);

        //relations based on measurements
        if (relations == null)
            setOutput(OUT_RELATIONS_TITLE, performRelationMapping(domainFeatures, rangeFeatures, measurements));
            //relations based on measurements attached to existing relations
        else
            setOutput(OUT_RELATIONS_TITLE, performRelationMapping(domainFeatures, rangeFeatures, relations, dropRelations.resolve()));

    }

    /**
     * perform relation mapping
     *
     * @param domainFeatures domain features
     * @param rangeFeatures  range features
     * @param measurements   relation measurements
     * @return feature relations
     */
    private BinaryRelationCollection performRelationMapping(@NotNull GTFeatureCollection domainFeatures, @NotNull GTFeatureCollection rangeFeatures, @Nullable RelationMeasurementCollection measurements) {
        BinaryRelationCollection relations = new BinaryRelationCollection(new ResourceIdentifier(), null, null);
        for (GTVectorFeature domain : domainFeatures) {
            for (GTVectorFeature range : rangeFeatures) {
                Set<IRelationMeasurement> associatedMeasurements = getAssociatedMeasurements(domain, range, measurements);
                BinaryRelation relation = performRelationMapping(domain, range, associatedMeasurements);
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
    private Set<IRelationMeasurement> getAssociatedMeasurements(@NotNull GTVectorFeature domain, @NotNull GTVectorFeature range, @Nullable RelationMeasurementCollection measurements) {
        if (measurements == null)
            return null;
        Set<IRelationMeasurement> associatedWithDomain = measurements.getMeasurements(domain);
        Set<IRelationMeasurement> associatedWithRange = measurements.getMeasurements(range);
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
    private BinaryRelationCollection performRelationMapping(@NotNull GTFeatureCollection domainFeatures, @NotNull GTFeatureCollection rangeFeatures, @NotNull BinaryRelationCollection relations, boolean dropRelations) {
        BinaryRelationCollection newRelations = new BinaryRelationCollection(new ResourceIdentifier(), null, null);
        for (BinaryRelation relation : relations) {
            //check for feature identifier in domain and range features
            GTVectorFeature domain = domainFeatures.getMember(relation.getDomain().getIRI());
            GTVectorFeature range = rangeFeatures.getMember(relation.getRange().getIRI());
            if (domain == null || range == null)
                continue;
            //add relation
            BinaryRelation newRelation = performRelationMapping(domain, range, relation.getMeasurements());
            if (newRelation != null)
                newRelations.add(newRelation);
            else if(!dropRelations)
                newRelations.add(relation);
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
    public abstract BinaryRelation performRelationMapping(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature, @Nullable Set<IRelationMeasurement> measurements);

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_DOMAIN_TITLE, IN_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_RANGE_TITLE, IN_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_MEASUREMENTS_TITLE, IN_MEASUREMENTS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null,
                null);
        addInputConnector(IN_RELATIONS_TITLE, IN_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryRelationCollection.class)},
                null,
                null);
        addInputConnector(IN_DROP_RELATIONS_TITLE, IN_DROP_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_RELATIONS_TITLE,
                OUT_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryRelationCollection.class)
                },
                null);
    }

}
